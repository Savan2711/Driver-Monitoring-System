import pyrebase

config = {
  "apiKey": "journeyrecord-8ebe2",
  "authDomain": "journeyrecord-8ebe2.firebaseapp.com",
  "databaseURL": "https://journeyrecord-8ebe2-default-rtdb.firebaseio.com/",
  "storageBucket": "journeyrecord-8ebe2.appspot.com"
}

firebase = pyrebase.initialize_app(config)
db = firebase.database()

print("Send Data to Firebase Using Raspberry Pi")
print("—————————————-")
print()

data = ['46','47']

db.child("x").child("1-set").set(data)
db.child("x").child("2-push").push(data)
