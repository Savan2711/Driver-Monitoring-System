import time
import serial
from urllib.request import urlopen 
ser = serial.Serial('COM3',9600) 

while True:
    message = ser.readline()
    print(message)
    print(message[19:21])
    print(message[41:43]) 
    # response = urlopen('https://api.thingspeak.com/update?api_key=TPCGKBEUP4Q29B1R&fie ld1='+message[19:21]+'&field2='+message[41:43])
    # html = response.read()
    # time.sleep(0.5)