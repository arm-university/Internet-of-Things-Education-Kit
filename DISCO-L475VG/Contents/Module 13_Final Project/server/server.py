from flask import Flask, make_response, send_from_directory, render_template, request, jsonify
import pymysql
from model import MLP
from api import test_data_generator
from queue import Queue
from threading import Thread, Lock
from datetime import datetime
import torch
import numpy as np

select_all = ''' select * from history_records; '''
select_last_record = '''select id, activity, confidence, count from history_records order by id desc limit 1; '''
update_last_record = '''update history_records set end_time = "%s", confidence=%.4f, count=%d where id = "%d" '''
add_record = '''insert into history_records (activity, start_time, end_time, confidence, count) values ("%s", "%s", "%s", %.4f, 15);'''
model = MLP()
model.load_state_dict(torch.load("./model.pth"))
label_dict = {
    0:'walk',
    1:'run',
    2:'still'
}


real_time_pred = None
new_pred = False
lock = Lock()

app = Flask(__name__)

def evaluate_single_sample(sample):

    sample = sample.view(1, -1)
    y_hat = torch.exp(model(sample))
    # print("walking: {}\trunning: {}\tstill:{}".format(y_hat[0, 0], y_hat[0, 1], y_hat[0, 2]))
    return y_hat[0]

def record_history():
    global real_time_pred, new_pred
    conn = pymysql.connect('localhost', 'activity_manager', 'password', 'Motion')
    prediction_list = []
    for sample in test_data_generator():
        sample = torch.tensor(sample).to(torch.float32)
        y_hat = evaluate_single_sample(sample)
        with lock:
            real_time_pred = y_hat.tolist()
            new_pred = True
        prediction = y_hat.max(dim=0).indices.item()
        prediction_list.append([prediction, datetime.now().strftime("%Y-%m-%d %H:%M:%S")])
        if len(prediction_list) >= 15:
            print("update")
            start_time = prediction_list[0][1]
            end_time = prediction_list[-1][1]
            predictions = [i[0] for i in prediction_list]
            print(predictions)
            walk_count = predictions.count(0)
            run_count = predictions.count(1)
            still_count = predictions.count(2)
            max_count = np.max([walk_count, run_count, still_count])
            new_activity = label_dict[np.argmax([walk_count, run_count, still_count])]
            print(new_activity)
            with conn.cursor() as cursor:
                flag = cursor.execute(select_last_record)
                if flag == 1:
                    last_id, last_activity, confidence, count = cursor.fetchone()
                    print(last_activity)
                    if last_activity == new_activity:
                        new_confidence = (confidence * count + max_count)/(count + 15)
                        cursor.execute(update_last_record % (end_time, new_confidence, count+15, last_id))
                    else:
                        cursor.execute(add_record %(new_activity, start_time, end_time, max_count/15))
                else:
                    cursor.execute(add_record %(new_activity, start_time, end_time, max_count/15))
            conn.commit()
            with conn.cursor() as cursor:
                cursor.execute(select_all)
                print(cursor.fetchall())
            prediction_list.clear()


@app.route('/history', methods=["GET"])
def get_history():
    flask_conn = pymysql.connect('localhost', 'activity_manager', 'password', 'Motion')
    with flask_conn.cursor() as cursor:
        flag = cursor.execute(select_all)
        if flag == 0:
            return make_response("no history", 400)
        else:
            table = cursor.fetchall()
            print(table)
            return make_response(jsonify(table), 200)

@app.route('/real_time_pred', methods=["GET"])
def get_prediction():
    global new_pred, real_time_pred
    with lock:
        if new_pred:
            new_pred = False
            return make_response(jsonify(real_time_pred), 200)
        else    
            return make_response(jsonify("no update"), 400)


if __name__ == "__main__":
    t = Thread(target=record_history)
    t.start()
    app.run(host='0.0.0.0', port=2333)


