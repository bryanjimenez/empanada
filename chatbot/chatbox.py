#!/usr/bin/python
from twython import Twython, TwythonError, TwythonStreamer
import time

class MyStreamer(TwythonStreamer):
	
	def on_success(self, data):
    		if 'text' in data:
        		print data['text'].encode('utf-8')

	def on_error(self, status_code, data):
		print status_code

		# Want to stop trying to get data because of the error?
		# Uncomment the next line!
		# self.disconnect()

def get_oauthentication():
	import json
	
	twitter_data = open('oauth.json')
	oauth = json.load(twitter_data)
	twitter_data.close()
	
	return oauth


def Update_status_with(oauth, message):
		
	twitter = Twython(oauth['APP_KEY'], oauth['APP_SECRET'], oauth['ACCESS_TOKEN'], oauth['ACCESS_TOKEN_SECRET'])
	twitter.verify_credentials()
			
	try:
		pass # twitter.update_status(status=message)
	except TwythonError as e:
		print e
		
	
def Streamer():
	oauth = get_oauthentication()
	stream = MyStreamer(oauth['APP_KEY'], oauth['APP_SECRET'], oauth['ACCESS_TOKEN'], oauth['ACCESS_TOKEN_SECRET'])
	# stream.statuses.filter(track='gas')
	stream.user()


def test():
	time.sleep(10)


def Search_for(str):
	oauth = get_oauthentication()
	twitter = Twython(oauth['APP_KEY'], oauth['APP_SECRET'], oauth_version=2)
	ACCESS_TOKEN = twitter.obtain_access_token()
	twitter = Twython(oauth['APP_KEY'], access_token=ACCESS_TOKEN)
	
	search = twitter.search_gen(str)
	# search = twitter.get_home_timeline()
			
	for result in search:
		print result['text']
	

if __name__ == "__main__":
    Streamer()
    # Search_for("gas")
    # Update_status_with(get_oauthentication(), "This message")
