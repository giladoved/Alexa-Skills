/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beam;

import com.amazon.speech.slu.Slot;
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
import com.amazonaws.util.json.JSONObject;
import com.github.kevinsawicki.http.HttpRequest;
import java.net.URL;
import java.util.logging.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spacegeek.SpaceGeekSpeechlet;

/**
 *
 * @author Gilad
 */
public class BeamSpeechlet implements Speechlet {

    private static final Logger log = LoggerFactory.getLogger(SpaceGeekSpeechlet.class);

    private static final String PARSE_APP_ID = "aUY9JDotnKtAfZZBPYKPgmYO37tTvfMH72NMjuZi";
    private static final String PARSE_REST_API_ID = "YEmeSlyjizg76FI29T8sIfa7ReB3BOadGwX4vqCA";

    String user;
    String message;

    @Override
    public void onSessionStarted(SessionStartedRequest request, Session session) throws SpeechletException {
        log.info("onSessionStarted requestId={}, sessionId={}", request.getRequestId(),
                session.getSessionId());
    }

    @Override
    public SpeechletResponse onLaunch(LaunchRequest request, Session session) throws SpeechletException {
        log.info("onLaunch requestId={}, sessionId={}", request.getRequestId(),
                session.getSessionId());

        return handleFirstTime();
    }

    @Override
    public SpeechletResponse onIntent(IntentRequest request, Session session) throws SpeechletException {
        log.info("onIntent requestId={}, sessionId={}", request.getRequestId(),
                session.getSessionId());

        Intent intent = request.getIntent();
        String intentName = (intent != null) ? intent.getName() : null;

        if ("MessageBeam".equals(intentName)) {
            Slot slot = request.getIntent().getSlot("User");
            user = slot.getValue().toLowerCase();

            String speechText = "What would you like to send " + user + "?";

            PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
            speech.setText(speechText);

            Reprompt reprompt = new Reprompt();
            reprompt.setOutputSpeech(speech);
            
            try {
                postRequestSendUsers();
            } catch (Exception ex) {
                java.util.logging.Logger.getLogger(BeamSpeechlet.class.getName()).log(Level.SEVERE, null, ex);
            }

            return SpeechletResponse.newAskResponse(speech, reprompt);
        } else if ("SentMessage".equals(intentName)) {
            String speechText = "Beam sent";
            PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
            speech.setText(speechText);

            Slot messageSlot = intent.getSlot("Message");
            message = messageSlot.getValue();
            String cardText = "You sent: " + message + " to " + user;
            SimpleCard card = new SimpleCard();
            card.setTitle("Beam");
            card.setContent(cardText);

            Reprompt reprompt = new Reprompt();
            reprompt.setOutputSpeech(speech);
            
            try {
                postRequestUpdateRecording();
            } catch (Exception ex) {
                java.util.logging.Logger.getLogger(BeamSpeechlet.class.getName()).log(Level.SEVERE, null, ex);
            }

            return SpeechletResponse.newAskResponse(speech, reprompt, card);
        } else if ("AMAZON.HelpIntent".equals(intentName)) {
            String speechText = "Beam a friend by saying 'beam friend'";
            PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
            speech.setText(speechText);
            Reprompt reprompt = new Reprompt();
            reprompt.setOutputSpeech(speech);
            return SpeechletResponse.newAskResponse(speech, reprompt);
        } else if ("AMAZON.StopIntent".equals(intentName)) {
            PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
            outputSpeech.setText("Goodbye");

            return SpeechletResponse.newTellResponse(outputSpeech);
        } else if ("AMAZON.CancelIntent".equals(intentName)) {
            PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
            outputSpeech.setText("Goodbye");

            return SpeechletResponse.newTellResponse(outputSpeech);
        } else {
            throw new SpeechletException("Invalid Intent");
        }
    }

    @Override
    public void onSessionEnded(SessionEndedRequest request, Session session) throws SpeechletException {
        log.info("onSessionEnded requestId={}, sessionId={}", request.getRequestId(),
                session.getSessionId());
    }

    private SpeechletResponse handleFirstTime() {
        String speechText = "Who would you like to beam?";

        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(speechText);

        Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(speech);

        return SpeechletResponse.newAskResponse(speech, reprompt);
    }
    
    private void postRequestSendUsers() throws Exception {
        String url = "https://api.parse.com/1/functions/saveUsers";
        URL obj = new URL(url);

        //Attempt to use HttpRequest to send post request to parse cloud
        HttpRequest request = HttpRequest.post(obj).contentType("application/json");
        request.header("X-Parse-Application-Id", PARSE_APP_ID);
        request.header("X-Parse-REST-API-Key", PARSE_REST_API_ID);
        JSONObject jsonParam = new JSONObject();
        jsonParam.put("from", "dave");
        jsonParam.put("to", user);
        request.send(jsonParam.toString().getBytes("UTF8"));

        if (request.ok()) {
            System.out.println("HttpRequest WORKED");
        } else {
            System.out.println("HttpRequest FAILED " + request.code() + request.body());
        }
    }

    private void postRequestUpdateRecording() throws Exception {
        String url = "https://api.parse.com/1/functions/updatemp3";
        URL obj = new URL(url);

        //Attempt to use HttpRequest to send post request to parse cloud
        HttpRequest request = HttpRequest.post(obj).contentType("application/json");
        request.header("X-Parse-Application-Id", PARSE_APP_ID);
        request.header("X-Parse-REST-API-Key", PARSE_REST_API_ID);
        JSONObject jsonParam = new JSONObject();
        jsonParam.put("username", "dave");
        request.send(jsonParam.toString().getBytes("UTF8"));

        if (request.ok()) {
            System.out.println("HttpRequest WORKED");
        } else {
            System.out.println("HttpRequest FAILED " + request.code() + request.body());
        }
    }

}
