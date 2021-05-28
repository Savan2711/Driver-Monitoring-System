import os
import sys
import pyrebase
from datetime import datetime
from time import sleep

config = {
    "apiKey": "AIzaSyCjJ5tH7M25I9NexIDXfoj5QU4DnSLoKK4",
    "authDomain": "locationtracer-66f3b.firebaseapp.com",
    "databaseURL": "https://locationtracer-66f3b-default-rtdb.firebaseio.com/",
    "projectId": "locationtracer-66f3b",
    "storageBucket": "locationtracer-66f3b.appspot.com",
    "messagingSenderId": "14166232665",
    "appId": "1:14166232665:android:80902de538fab8988f6b48"
};

firebase = pyrebase.initialize_app(config)
database = firebase.database()
                 
cords1 = database.child("Coordinates")
x = cords1.child("lat").get().val()
        
cords2 = database.child("Coordinates")
y = cords2.child("lon").get().val()


startingX = x
startingY = y

now = datetime.now()
FMT = '%H:%M:%S'

stime = now.strftime("%H:%M:%S")
print(stime)


lastX = 0
lastY = 0

try:
    while True:

        database = firebase.database()
        
        flags = database.child("Coordinates")
        journeyOn = flags.child("isJourneyOn").get().val()
        
        cords1 = database.child("Coordinates")
        x = cords1.child("lat").get().val()
        
        cords2 = database.child("Coordinates")
        y = cords2.child("lon").get().val()
        
        print(journeyOn)
        
        if journeyOn != 145:
            
            import eyeYawn
            
            lastX = x
            lastY = y
            now = datetime.now()
            ltime = now.strftime("%H:%M:%S")
            break  
     
    answer = str(startingX) +  ' ' +  str(startingY) + ' ' + str(lastX) +  ' ' +  str(lastY)

    answer = answer + ' ' + stime + ' ' + ltime
    
    td = datetime.strptime(ltime, FMT) - datetime.strptime(stime, FMT)
    
    td = str(td)
    
    tempTime = td.split(':')
      
    timeDiff = (int(tempTime[0]) * 3600) + ( int(tempTime[1]) * 60 ) + int(tempTime[2])
        
    answer = answer + ' ' + str(timeDiff)

    f1 = open("eyeYawnOutput.txt","r+") 
    temp = f1.read()
    f1.close()
    
    eye,yawn = temp.split()

    answer = answer + ' ' + eye + ' ' + yawn
    
    config = { "apiKey": "journeyrecord-8ebe2",
               "authDomain": "journeyrecord-8ebe2.firebaseapp.com",
               "databaseURL": "https://journeyrecord-8ebe2-default-rtdb.firebaseio.com/",
               "storageBucket": "journeyrecord-8ebe2.appspot.com"}
    
    fb = pyrebase.initialize_app(config)
    db = fb.database()
    
    ans = list(answer.strip().split())
    
    data = { 'Source latitude'  : ans[0],
             'Source longitude' : ans[1],
             'Destination latitude' : ans[2],
             'Destination longitude' : ans[3],
             'Start Time' : ans[4],
             'End Time' : ans[5],
             'Time Diffrence' : ans[6],
             'Eye Counter' : ans[7],
             'Yawn Counter' : ans[8]
        
        }
    
        
    db.child("Data").set(data)


except:
    print('none')
