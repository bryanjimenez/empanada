#!/usr/bin/python
class ChatBot():
    def __init__(self):
        self.mentions = []
        self.question = False

    def add_to_mention(self, tweet):
        self.mentions.append(tweet)

    def respond_to_mention(self):
        if self.mentions:
            print "Geolocation is " + ("" if self.mentions.pop(0)['user']['geo_enabled'] else "not ") + "enabled"

if __name__ == "__main__":
    bot = ChatBot()
    bot.add_to_mention({"2": "4"})
    bot.add_to_mention({"6": "4"})
    bot.add_to_mention({"9": "4"})
    bot.add_to_mention({"2": "5"})
    bot.add_to_mention({"4": "4"})    
    bot.respond_to_mention()