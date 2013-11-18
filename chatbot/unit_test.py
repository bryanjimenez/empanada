'''
Created on Nov 16, 2013

@author: santiago
'''
import unittest
from ChatBotClass import ChatBot


class Test(unittest.TestCase):


    def setUp(self):
        self.chat_bot = ChatBot()

#     def test_analyze_question(self):
#         expected = [-50, -50]
#         question = ["@empanada where is my water?",
#                     "@empaanda where is my fire?"]
#         count = 0;
#         
#                        
#         for q in question:            
#             result = self.chat_bot.analyze_question(q)
#             self.assertEqual(result, expected[count], "PASS")
#             count += 1

    def test_analyze_question_1(self):               
        result = self.chat_bot.analyze_question("@empanada where is my water?")
        self.assertEqual(result, -50, result)
    
    def test_analyze_question_2(self):               
        result = self.chat_bot.analyze_question("@empanada water status?")
        if result in [-55, -54]:
            self.assertEqual(True, True, result)
        else:
            self.assertEqual(False, True, result)
    
    def test_analyze_question_3(self):               
        result = self.chat_bot.analyze_question("@empanada WHAT IS THE CLOSEST GAS station?")
        self.assertEqual(result, -50, result)
    
    def test_analyze_question_4(self):               
        result = self.chat_bot.analyze_question("@empanada ##########")
        self.assertEqual(result, 0, result)
    
    def test_analyze_question_5(self):               
        result = self.chat_bot.analyze_question("@empanada Fire Status?")
        if result in [-55, -54]:
            self.assertEqual(True, True, result)
        else:
            self.assertEqual(False, True, result)

    # test whether the tweet should be added to the queue
    # this shouls return -1 for NO
    # becuase is doesn't contain @empanada305
    def test_add_to_mention_1(self):
        tweet = {"contributors":None,"truncated":False,"text":"Shout out to the gas station next to treehouse you saved my life!","in_reply_to_status_id":None,"id":401727051755188224,"favorite_count":0,"source":"<a href=\"http:\/\/twitter.com\/download\/iphone\" rel=\"nofollow\">Twitter for iPhone<\/a>","retweeted":False,"coordinates":{"type":"Point","coordinates":[-80.34580632,25.77846391]},"entities":{"symbols":[],"user_mentions":[],"hashtags":[],"urls":[]},"in_reply_to_screen_name":None,"id_str":"401727051755188224","retweet_count":0,"in_reply_to_user_id":None,"favorited":False,"user":{"follow_request_sent":None,"profile_use_background_image":True,"default_profile_image":False,"id":391786711,"verified":False,"profile_image_url_https":"https:\/\/pbs.twimg.com\/profile_images\/378800000708808801\/30976398074d0c10cc4f3979daf2a968_normal.jpeg","profile_sidebar_fill_color":"E6F6F9","profile_text_color":"333333","followers_count":336,"profile_sidebar_border_color":"DBE9ED","id_str":"391786711","profile_background_color":"DBE9ED","listed_count":0,"profile_background_image_url_https":"https:\/\/abs.twimg.com\/images\/themes\/theme17\/bg.gif","utc_offset":-18000,"statuses_count":15187,"description":"#ymcmb #FIU","friends_count":250,"location":"","profile_link_color":"CC3366","profile_image_url":"http:\/\/pbs.twimg.com\/profile_images\/378800000708808801\/30976398074d0c10cc4f3979daf2a968_normal.jpeg","following":None,"geo_enabled":True,"profile_banner_url":"https:\/\/pbs.twimg.com\/profile_banners\/391786711\/1382051265","profile_background_image_url":"http:\/\/abs.twimg.com\/images\/themes\/theme17\/bg.gif","name":"Mckaila Giammanco ","lang":"en","profile_background_tile":False,"favourites_count":1239,"screen_name":"Mckaila_gee","notifications":None,"url":None,"created_at":"Sun Oct 16 03:15:30 +0000 2011","contributors_enabled":False,"time_zone":"Quito","protected":False,"default_profile":False,"is_translator":False},"geo":{"type":"Point","coordinates":[25.77846391,-80.34580632]},"in_reply_to_user_id_str":None,"lang":"en","created_at":"Sat Nov 16 15:02:47 +0000 2013","filter_level":"medium","in_reply_to_status_id_str":None,"place":{"full_name":"Fountainbleau, FL","url":"https:\/\/api.twitter.com\/1.1\/geo\/id\/07dbf3c95d1664ed.json","country":"United States","place_type":"city","bounding_box":{"type":"Polygon","coordinates":[[[-80.385902,25.761281],[-80.385902,25.7821],[-80.319378,25.7821],[-80.319378,25.761281]]]},"contained_within":[],"country_code":"US","attributes":{},"id":"07dbf3c95d1664ed","name":"Fountainbleau"}}
        result = self.chat_bot.add_to_mention(tweet)
        self.assertEqual(result, -1, result)
    
    # test whether the tweet should be added to the queue
    # this shouls return 0 for YES        
    def test_add_to_mention_2(self):
        tweet = {"contributors":None,"truncated":False,"text":"@empanada305 water status?","in_reply_to_status_id":None,"id":401727051755188224,"favorite_count":0,"source":"<a href=\"http:\/\/twitter.com\/download\/iphone\" rel=\"nofollow\">Twitter for iPhone<\/a>","retweeted":False,"coordinates":{"type":"Point","coordinates":[-80.34580632,25.77846391]},"entities":{"symbols":[],"user_mentions":[],"hashtags":[],"urls":[]},"in_reply_to_screen_name":None,"id_str":"401727051755188224","retweet_count":0,"in_reply_to_user_id":None,"favorited":False,"user":{"follow_request_sent":None,"profile_use_background_image":True,"default_profile_image":False,"id":391786711,"verified":False,"profile_image_url_https":"https:\/\/pbs.twimg.com\/profile_images\/378800000708808801\/30976398074d0c10cc4f3979daf2a968_normal.jpeg","profile_sidebar_fill_color":"E6F6F9","profile_text_color":"333333","followers_count":336,"profile_sidebar_border_color":"DBE9ED","id_str":"391786711","profile_background_color":"DBE9ED","listed_count":0,"profile_background_image_url_https":"https:\/\/abs.twimg.com\/images\/themes\/theme17\/bg.gif","utc_offset":-18000,"statuses_count":15187,"description":"#ymcmb #FIU","friends_count":250,"location":"","profile_link_color":"CC3366","profile_image_url":"http:\/\/pbs.twimg.com\/profile_images\/378800000708808801\/30976398074d0c10cc4f3979daf2a968_normal.jpeg","following":None,"geo_enabled":True,"profile_banner_url":"https:\/\/pbs.twimg.com\/profile_banners\/391786711\/1382051265","profile_background_image_url":"http:\/\/abs.twimg.com\/images\/themes\/theme17\/bg.gif","name":"Mckaila Giammanco ","lang":"en","profile_background_tile":False,"favourites_count":1239,"screen_name":"Mckaila_gee","notifications":None,"url":None,"created_at":"Sun Oct 16 03:15:30 +0000 2011","contributors_enabled":False,"time_zone":"Quito","protected":False,"default_profile":False,"is_translator":False},"geo":{"type":"Point","coordinates":[25.77846391,-80.34580632]},"in_reply_to_user_id_str":None,"lang":"en","created_at":"Sat Nov 16 15:02:47 +0000 2013","filter_level":"medium","in_reply_to_status_id_str":None,"place":{"full_name":"Fountainbleau, FL","url":"https:\/\/api.twitter.com\/1.1\/geo\/id\/07dbf3c95d1664ed.json","country":"United States","place_type":"city","bounding_box":{"type":"Polygon","coordinates":[[[-80.385902,25.761281],[-80.385902,25.7821],[-80.319378,25.7821],[-80.319378,25.761281]]]},"contained_within":[],"country_code":"US","attributes":{},"id":"07dbf3c95d1664ed","name":"Fountainbleau"}}
        result = self.chat_bot.add_to_mention(tweet)
        self.assertEqual(result, 0, result)
            
    # test whether the tweet should be added to the queue
    # this shouls return -1 for NO
    # because i doesn't contain @empanada305
    def test_add_to_mention_3(self):
        tweet = {"contributors":None,"truncated":False,"text":"water status?","in_reply_to_status_id":None,"id":401727051755188224,"favorite_count":0,"source":"<a href=\"http:\/\/twitter.com\/download\/iphone\" rel=\"nofollow\">Twitter for iPhone<\/a>","retweeted":False,"coordinates":{"type":"Point","coordinates":[-80.34580632,25.77846391]},"entities":{"symbols":[],"user_mentions":[],"hashtags":[],"urls":[]},"in_reply_to_screen_name":None,"id_str":"401727051755188224","retweet_count":0,"in_reply_to_user_id":None,"favorited":False,"user":{"follow_request_sent":None,"profile_use_background_image":True,"default_profile_image":False,"id":391786711,"verified":False,"profile_image_url_https":"https:\/\/pbs.twimg.com\/profile_images\/378800000708808801\/30976398074d0c10cc4f3979daf2a968_normal.jpeg","profile_sidebar_fill_color":"E6F6F9","profile_text_color":"333333","followers_count":336,"profile_sidebar_border_color":"DBE9ED","id_str":"391786711","profile_background_color":"DBE9ED","listed_count":0,"profile_background_image_url_https":"https:\/\/abs.twimg.com\/images\/themes\/theme17\/bg.gif","utc_offset":-18000,"statuses_count":15187,"description":"#ymcmb #FIU","friends_count":250,"location":"","profile_link_color":"CC3366","profile_image_url":"http:\/\/pbs.twimg.com\/profile_images\/378800000708808801\/30976398074d0c10cc4f3979daf2a968_normal.jpeg","following":None,"geo_enabled":True,"profile_banner_url":"https:\/\/pbs.twimg.com\/profile_banners\/391786711\/1382051265","profile_background_image_url":"http:\/\/abs.twimg.com\/images\/themes\/theme17\/bg.gif","name":"Mckaila Giammanco ","lang":"en","profile_background_tile":False,"favourites_count":1239,"screen_name":"Mckaila_gee","notifications":None,"url":None,"created_at":"Sun Oct 16 03:15:30 +0000 2011","contributors_enabled":False,"time_zone":"Quito","protected":False,"default_profile":False,"is_translator":False},"geo":{"type":"Point","coordinates":[25.77846391,-80.34580632]},"in_reply_to_user_id_str":None,"lang":"en","created_at":"Sat Nov 16 15:02:47 +0000 2013","filter_level":"medium","in_reply_to_status_id_str":None,"place":{"full_name":"Fountainbleau, FL","url":"https:\/\/api.twitter.com\/1.1\/geo\/id\/07dbf3c95d1664ed.json","country":"United States","place_type":"city","bounding_box":{"type":"Polygon","coordinates":[[[-80.385902,25.761281],[-80.385902,25.7821],[-80.319378,25.7821],[-80.319378,25.761281]]]},"contained_within":[],"country_code":"US","attributes":{},"id":"07dbf3c95d1664ed","name":"Fountainbleau"}}
        result = self.chat_bot.add_to_mention(tweet)
        self.assertEqual(result, -1, result)
     
    def test_generate_message(self):
        result = self.chat_bot.generate_message()
        if ("Please refer to http://empanada.cs.fiu.edu" in result):
            self.assertEqual(True, True, result)
        else:
            self.assertEqual(False, False, result)
    

if __name__ == "__main__":
    #import sys;sys.argv = ['', 'Test.testName']
    unittest.main()