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

    def test_add_to_mention(self):
        result = self.chat_bot.add_to_mention("@empanada Fire Status?")
        if result in [-55, -54]:
            self.assertEqual(True, True, result)
        else:
            self.assertEqual(False, True, result)


if __name__ == "__main__":
    #import sys;sys.argv = ['', 'Test.testName']
    unittest.main()