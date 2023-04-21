import torch
from torch import nn, optim
from api import test_data_generator
from torch.utils.data import Dataset, DataLoader
from torch.nn import functional as F
import pandas as pd
import numpy as np
from sklearn import metrics


class MLP(nn.Module):
    def __init__(self):
        super(MLP, self).__init__()
        self.classifier = nn.Sequential(
            nn.Linear(12, 24),
            nn.Sigmoid(),
            nn.Linear(24, 16),
            nn.Sigmoid(),
            nn.Linear(16, 3),
            nn.LogSoftmax()
        )

    def forward(self, x):
        return F.log_softmax(self.classifier(x), dim=1)


class SimpleDataset(Dataset):
    def __init__(self, data_path):
        self.dataset = pd.read_csv(data_path)
        self.X = torch.tensor(self.dataset.iloc[:, :-1].values).to(torch.float32)
        self.y = torch.tensor(self.dataset.iloc[:, -1].values).to(int)

    def __len__(self):
        return len(self.y)

    def __getitem__(self, index):
        return self.X[index], self.y[index]



def loss_function(y_hat, y):
    return F.nll_loss(y_hat, y, reduction='sum')


def train(epoch):
    model.train()
    train_loss = 0
    for batch_idx, (x, y) in enumerate(train_loader):
        optimizer.zero_grad()
        y_hat = model(x)
        loss = loss_function(y_hat, y)
        loss.backward()
        train_loss += loss.item()
        optimizer.step()
    loss_str = '====> Epoch: {} Average loss: {:.4f}'.format(
        epoch, train_loss / len(train_loader.dataset))
    print(loss_str)


def test():
    global best_loss
    model.eval()
    test_loss = 0
    with torch.no_grad():
        for idx, (x, y) in enumerate(test_loader):
            y_hat = model(x)
            loss = loss_function(y_hat, y)
            test_loss += loss.item()
        if test_loss < best_loss:
            best_loss = test_loss
            torch.save(model.state_dict(), "./model.pth")
            print("best")
        loss_str = '====> Average loss: {:.4f}'.format(
            test_loss / len(test_loader.dataset))
        print(loss_str)


def evaluate():
    model.eval()
    total_y_hat = []
    total_y = []
    with torch.no_grad():
        for x, y in test_loader:
            y_hat = model(x)
            y_hat = y_hat.max(dim=1).indices.view(-1, 1)
            y = y.view(-1, 1)
            total_y_hat.extend(y_hat.to(int).tolist())
            total_y.extend(y.to(int).tolist())

    print(metrics.confusion_matrix(total_y, total_y_hat))

    print(metrics.classification_report(total_y, total_y_hat))


def evaluate_single_sample(sample):

    sample = sample.view(1, -1)
    y_hat = torch.exp(model(sample))
    # print("walking: {}\trunning: {}\tstill:{}".format(y_hat[0, 0], y_hat[0, 1], y_hat[0, 2]))
    return y_hat[0]     


if __name__ == '__main__':
    model = MLP()
    optimizer = optim.Adam(model.parameters(), lr=1e-3)
    train_loader = DataLoader(SimpleDataset("train.csv"), batch_size=128, shuffle=True)
    test_loader = DataLoader(SimpleDataset("test.csv"), batch_size=128, shuffle=True)
    best_loss = np.inf

    # for epoch in range(20):
    #     train(epoch)
    #     test()
    #     evaluate()
    model.load_state_dict(torch.load("./model.pth"))
    for sample in test_data_generator():
        sample = torch.tensor(sample).to(torch.float32)
        evaluate_single_sample(sample)
