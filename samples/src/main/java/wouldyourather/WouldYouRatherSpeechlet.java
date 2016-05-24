/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wouldyourather;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SessionEndedRequest;
import com.amazon.speech.speechlet.SessionStartedRequest;
import com.amazon.speech.speechlet.Speechlet;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SimpleCard;
import com.amazon.speech.ui.SsmlOutputSpeech;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author gilad
 */
public class WouldYouRatherSpeechlet implements Speechlet {
    
    private static final Logger log = LoggerFactory.getLogger(WouldYouRatherSpeechlet.class);

    private static final String SESSION_QUESTION_ID = "questionid";
    
    private static final ArrayList<Question> QUESTION_LIST = new ArrayList<>();
    
    static {
        QUESTION_LIST.add(new Question("be four foot tall", "be ten feet tall.", 44, 1454625));
        QUESTION_LIST.add(new Question("be covered in feathers", "covered in fur.", 29, 1208961));
        QUESTION_LIST.add(new Question("see blurry all the time", "seeing everything in black and white.", 22, 1031974));
        QUESTION_LIST.add(new Question("have two sets of twins", "have quadruplets.", 78, 1460199));
        QUESTION_LIST.add(new Question("constantly have a 40 pound weight on your shoulders", "constantly have 10 pound weights on each of your feet.", 27, 1215295));
        QUESTION_LIST.add(new Question("become a pokemon trainer", "attend hogwarts.", 49, 1038630));
        QUESTION_LIST.add(new Question("be a rich but stupid athlete", "be a genius but poor scientist.", 50, 1119427));
        QUESTION_LIST.add(new Question("live until 80 in poverty", "live until 40 in riches.", 34, 1504450));
        QUESTION_LIST.add(new Question("see how or what created civilization", "see how or what ends civilization.", 46, 1469967));
        QUESTION_LIST.add(new Question("fight a shark with bear arms", " fight a bear with shark arms.", 56, 1253263));
        QUESTION_LIST.add(new Question("always know when people are lying", "always get away with lying.", 57, 1399738));
        QUESTION_LIST.add(new Question("be the best singer in the world but ugly", "be the worst singer in the world but beautiful.", 37, 1075036));
        QUESTION_LIST.add(new Question("be an excellent singer", "be an excellent writer.", 62, 1426541));
        QUESTION_LIST.add(new Question("be godzilla", "be king kong.", 57, 1282864));
        QUESTION_LIST.add(new Question("fly like superman", "travel like spiderman.", 68, 1229216));
        QUESTION_LIST.add(new Question("have chocolate ice cream", "have vanilla ice cream.", 61, 1179958));
        QUESTION_LIST.add(new Question("be a mermaid", "be a princess.", 49, 611819));
        QUESTION_LIST.add(new Question("be a cat", "be a dog.", 37, 1573515));
        QUESTION_LIST.add(new Question("stop using youtube", "stop using Facebook.", 19, 1437834));
        QUESTION_LIST.add(new Question("sound like anyone you want", "speak any language you want.", 29, 1171936));
        QUESTION_LIST.add(new Question("live in the past", "live in the future.", 29, 1422423));
    }
    
    @Override
    public void onSessionStarted(SessionStartedRequest request, Session session) throws SpeechletException {
        log.info("onSessionStarted request={}, sessionId={}", request.getRequestId(), session.getSessionId());
    }

    @Override
    public SpeechletResponse onLaunch(LaunchRequest request, Session session) throws SpeechletException {
        log.info("onLaunch request={}, sessionId={}", request.getRequestId(), session.getSessionId());
        
        return handleTellMeAQuestion(session);
    }

    @Override
    public SpeechletResponse onIntent(IntentRequest request, Session session) throws SpeechletException {
        log.info("onIntent request={}, sessionId={}", request.getRequestId(), session.getSessionId());
        
        Intent intent = request.getIntent();
        String intentName = (intent != null) ? intent.getName() : null;
        
        if ("TellMeAQuestion".equals(intentName)) {
            return handleTellMeAQuestion(session);
        } else if ("RepeatAgain".equals(intentName)) {
            return handleRepeatAgain(session);
        } else if ("YesGiveResultsAskAgain".equals(intentName)) {
            return handleYesGiveResultsAskAgain(session);
        } else if ("NoGiveResultsAskAgain".equals(intentName)) {
            return handleNoGiveResultsAskAgain(session);
        } else if ("AMAZON.StopIntent".equals(intentName)) {
            PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
            outputSpeech.setText("Goodbye");
            return SpeechletResponse.newTellResponse(outputSpeech);
        } else if ("AMAZON.CancelIntent".equals(intentName)) {
            PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
            outputSpeech.setText("Goodbye");
            return SpeechletResponse.newTellResponse(outputSpeech);
        } else if ("AMAZON.HelpIntent".equals(intentName)) {
            String speechText = "You can ask me to start a would you rather game by saying 'let's play would you rather'";
            PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
            speech.setText(speechText);
            Reprompt reprompt = new Reprompt();
            reprompt.setOutputSpeech(speech);
            return SpeechletResponse.newAskResponse(speech, reprompt);
        } else {
            throw new SpeechletException("Invalid Intent");
        }
    }

    @Override
    public void onSessionEnded(SessionEndedRequest request, Session session) throws SpeechletException {
        log.info("onSessionEnded request={}, sessionId={}", request.getRequestId(), session.getSessionId());
    }

    private SpeechletResponse handleTellMeAQuestion(Session session) {
        int questionID = (int) Math.floor(Math.random() * QUESTION_LIST.size());
        Question question =  QUESTION_LIST.get(questionID);
        
        session.setAttribute(SESSION_QUESTION_ID, questionID);
        
        String repromptText = "Answer 'yes' if you would, 'no' if you would not";
        PlainTextOutputSpeech repromptSpeech = new PlainTextOutputSpeech();
        repromptSpeech.setText(repromptText);
        Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(repromptSpeech);
        
        String cardText = "You would rather " + question.questionOne + " than " + question.questionTwo + " Is that true?";
        String speechText = "<speak> You would rather " + question.questionOne + " <break time=\".75s\"/> than " + question.questionTwo + " Is that true?</speak>";
        SsmlOutputSpeech speech = new SsmlOutputSpeech();
        speech.setSsml(speechText);
        
        SimpleCard card = new SimpleCard();
        card.setTitle("Would you rather");
        card.setContent(cardText);
        
        SpeechletResponse response = SpeechletResponse.newAskResponse(speech, reprompt, card);
        return response;
    }

    private SpeechletResponse handleYesGiveResultsAskAgain(Session session) {
        String speechText;
        String cardText = "";
        if (session.getAttributes().containsKey(SESSION_QUESTION_ID)) {
            int questionID = (Integer) session.getAttribute(SESSION_QUESTION_ID);
            Question question = QUESTION_LIST.get(questionID);
            speechText = question.firstPercentage + " percent agree with you. If you would like another one say 'next', otherwise say 'quit'.";
            cardText = question.firstPercentage + "% of the people would rather " + question.questionOne + question.totalVoters + " people voted in this round of would you rather.";
        } else {
            speechText = "Sorry, something went wrong. To try again you can say, let's play would you rather.";
        }
        
        SimpleCard card = new SimpleCard();
        card.setTitle("Would you rather");
        card.setContent(cardText);
        
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(speechText);
        
        Reprompt reprompt = new Reprompt();
        PlainTextOutputSpeech repromptSpeech = new PlainTextOutputSpeech();
        repromptSpeech.setText("If you would like another one say 'next', otherwise say 'quit'.");
        reprompt.setOutputSpeech(repromptSpeech);
        
        return SpeechletResponse.newAskResponse(speech, reprompt, card);
    }
 
    private SpeechletResponse handleNoGiveResultsAskAgain(Session session) {
        String speechText;
        String cardText = "";
        if (session.getAttributes().containsKey(SESSION_QUESTION_ID)) {
            int questionID = (Integer) session.getAttribute(SESSION_QUESTION_ID);
            Question question = QUESTION_LIST.get(questionID);
            speechText = (100 - question.firstPercentage) + " percent agree with you. If you would like another one say 'next', otherwise say 'quit'.";
            cardText = (100 - question.firstPercentage) + "% of the people would rather " + question.questionTwo + question.totalVoters + " people voted in this round of would you rather.";
        } else {
            speechText = "Sorry, something went wrong. To try again you can say, let's play would you rather.";
        }
        
        SimpleCard card = new SimpleCard();
        card.setTitle("Would you rather");
        card.setContent(cardText);
        
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(speechText);
        
        Reprompt reprompt = new Reprompt();
        PlainTextOutputSpeech repromptSpeech = new PlainTextOutputSpeech();
        repromptSpeech.setText("If you would like another one say 'next', otherwise say 'quit'.");
        reprompt.setOutputSpeech(repromptSpeech);
        
        return SpeechletResponse.newAskResponse(speech, reprompt, card);
    }

    private SpeechletResponse handleRepeatAgain(Session session) {
        if (session.getAttributes().containsKey(SESSION_QUESTION_ID)) {
            int questionID = (Integer) session.getAttribute(SESSION_QUESTION_ID);
            Question question = QUESTION_LIST.get(questionID);
            String speechText = "<speak>You would rather " + question.questionOne + " <break time=\".75s\"/> than " + question.questionTwo + " Is that true?</speak>";
            SsmlOutputSpeech speech = new SsmlOutputSpeech();
            speech.setSsml(speechText);

            Reprompt reprompt = new Reprompt();
            SsmlOutputSpeech repromptSpeech = new SsmlOutputSpeech();
            repromptSpeech.setSsml(speechText);
            reprompt.setOutputSpeech(repromptSpeech);

            SpeechletResponse response = SpeechletResponse.newAskResponse(speech, reprompt);
            return response;
        }
        
        return null;
    }
    
    private static class Question {
        private final String questionOne;
        private final String questionTwo;
        private final int firstPercentage;
        private final long totalVoters;

        Question(String questionOne, String questionTwo, int firstPercentage, long totalVoters) {
            this.questionOne = questionOne;
            this.questionTwo = questionTwo;
            this.firstPercentage = firstPercentage;
            this.totalVoters = totalVoters;
        }
    }
}
