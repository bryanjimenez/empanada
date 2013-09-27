#!/usr/bin/python
from twython import Twython, TwythonError
import time

def EMPa():
	APP_KEY = 'i8yi6oyp4UtwoSGVBrqiVQ'
	APP_SECRET = 'SXGkWmdvjRYvWRCHUDhJSTkkI5uVhXkm8xHjgCdSw'
	ACCESS_TOKEN = "392397306-fgFNbrKfccVImUMLe64d6cgdy4gxffR8Tqc1pTCT"
	ACCESS_TOKEN_SECRET = "D2xjntYtbhL3jlPJwrKOhaYkxI99r5B1XWzNFMFc"
	
	twitter = Twython(APP_KEY, APP_SECRET, oauth_version=1)
	oauth = twitter.get_authentication_tokens()
	twitter = Twython(APP_KEY, APP_SECRET, oauth['oauth_token'], oauth['oauth_token_secret'],
					ACCESS_TOKEN)
			
	try:
		twitter.update_status(status='See how easy this was?')
	except TwythonError as e:
		print e
		
	"""
	twitter = Twython(APP_KEY, APP_SECRET, oauth_version=2)
	ACCESS_TOKEN = twitter.obtain_access_token()
	twitter = Twython(APP_KEY, access_token=ACCESS_TOKEN)
	"""
	
	"""search = twitter.search_gen("python")

	search = twitter.get_home_timeline()
			
	for result in search:
		print result['text']
			
	time.sleep(4)
	"""

def test():
	time.sleep(10)

		

if __name__ == "__main__":
    EMPa()
