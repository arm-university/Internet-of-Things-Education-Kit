
def extract_gyro(line):
	readings = line.split(":")[1].split(",")
	return int(readings[1]), int(readings[3]), int(readings[5])


with open("/home/haoyu/screenlog.0", 'r') as f:
	gyro_x = gyro_y = gyro_z = 0
	counter = 0
	for line in f.readlines():
		x, y, z = extract_gyro(line)
		gyro_x += x
		gyro_y += y
		gyro_z += z
		counter += 1
	print(int(gyro_x/counter), int(gyro_y/counter), int(gyro_z/counter))