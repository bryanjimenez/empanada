from twython import Twython
from twython import TwythonStreamer
import sqlite3
import oauth
import ConfigParser

config = ConfigParser.ConfigParser()
config.read('oauth')

db = sqlite3.connect('tweets.sqlite')
cursor = db.cursor()

APP_KEY = config.get('twitter', 'APP_KEY')
APP_SECRET = config.get('twitter', 'APP_SECRET')
OAUTH_TOKEN = config.get('twitter', 'OAUTH_TOKEN')
OAUTH_TOKEN_SECRET = config.get('twitter', 'OAUTH_TOKEN_SECRET')

class MyStreamer(TwythonStreamer):
	def on_success(self, data):
		try:
			if 'text' in data:
				print data['user']['screen_name'].encode('utf-8') + ": " 
				print data['text'].encode('utf-8')
				print	
				a = data['user']['screen_name']
				b = data['text'].encode('utf-8')
				c = data['created_at']
				cursor.execute( "INSERT INTO tweets VALUES (\'" + a + "\', \'" + b + "\', \'" + c + "\')")
				db.commit()
		except:
			db.rollback()
			
	def on_error(self, status_code, data):
		print status_code
		self.disconnect()
		db.close()

        # Want to stop trying to get data because of the error?
        # Uncomment the next line!
        # self.disconnect()

stream = MyStreamer(APP_KEY, APP_SECRET, OAUTH_TOKEN, OAUTH_TOKEN_SECRET)

stream.statuses.filter(track = 'storm')
#~ stream.statuses.filter(locations = '-82.177734,24.491523,-79.736023,27.009891')
#~ stream.statuses.filter(follow = 'justinbieber')

###### UNCOMMENT TO USE THE REST/SEARCH API
#~ 
#~ twitter = Twython(APP_KEY, APP_SECRET,OAUTH_TOKEN, OAUTH_TOKEN_SECRET)
#~ 
#~ try:
    #~ user_timeline = twitter.get_user_timeline(screen_name='AwesomeAndy')
#~ except TwythonError as e:
    #~ print e
#~ 
#~ for tweet in user_timeline :
	#~ 
	#~ print tweet['user']['screen_name'] + ": " + tweet['text']
	#~ if tweet['place']:
		#~ print tweet['place']['name']



