package sv.controllers;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@PropertySource(value = { "classpath:network.properties" })
public class MainController {
	
	private static final Logger logger = LoggerFactory.getLogger(MainController.class);
	
	@Value("${predictor.host}")
	public String PREDICTOR_HOST;
	@Value("${predictor.port}")
	public int PREDICTOR_PORT;
	
	private static final String PREDICT_COMMAND = "PR";
	private static final String FEED_COMMAND = "FE";
	// Each FEED_FREQ minutes the web server will provide new images to the predictor
	private static final long FEED_FREQ = 5;
	
	private static final String[] NOTIFICATION_COLOR = { "#ff0000", "#0000ff" };
	private static final String[] NOTIFICATION_MSG = { "ALERT", "WARNING" };
	
	/**
	 * Predicts whether there is an alert or a warning in a camera
	 * @param camera Camera URI
	 * @param date Date of the wanted prediction
	 * @throws Exception 
	 */
	@RequestMapping(value = "/predict", method = RequestMethod.GET)
	public ResponseEntity<?> predict(@RequestParam(value = "camera", required = true) URI camera,
			@RequestParam(value = "date", required = true) Date date) throws Exception{
		
		return null;
	}
	
	/**
	 * Converts a double to a 8-char string in order to send it to the python server:
	 * - If the length is more than 8 then it gets the first 8 characters, hence it deletes
	 * the least significative decimal numbers.
	 * - Otherwise it prepends zeroes till the string has 8 characters.
	 * @param d Double to convert
	 * @return 8-char stringified double
	 */
	public static String fromDoubleToString(double d){
		String res = Double.toString(d);
		if (res.length() > 8) res = res.substring(0, 8);
		else{
			while(res.length() < 8){
				res = "0" + res;
			}
		}
		return res;
	}
	
	/**
	 * - If the int has more than 8 digits then it is returned (nothing is done)
	 * - Otherwise it is converted to a 8-char string in order to send it to the python server,
	 * prepending zeroes till the string has 8 characters.
	 * @param d Int to convert
	 * @return 8-char stringified int
	 */
	public static String fromIntToString(int i){
		String res = Integer.toString(i);
		while(res.length() < 8){
			res = "0" + res;
		}
		return res;
	}
	
	/**
	 * - If the long has more than 8 digits then it is returned (nothing is done)
	 * - Otherwise it is converted to a 8-char string in order to send it to the python server,
	 * prepending zeroes till the string has 8 characters.
	 * @param d Long to convert
	 * @return 8-char stringified long
	 */
	public static String fromLongToString(long i){
		String res = Long.toString(i);
		while(res.length() < 8){
			res = "0" + res;
		}
		return res;
	}
	
	//@Async
	//@Scheduled(fixedRate=FEED_FREQ*1000)
	/**
	 * Provides new information about flights to the predictor
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public boolean feedPredictor() throws UnknownHostException, IOException{
		boolean res = true;
		Socket predictorSocket = new Socket(PREDICTOR_HOST, PREDICTOR_PORT);
		logger.info("Connected to the predictor");
		PrintWriter output = new PrintWriter(predictorSocket.getOutputStream(), false);
		BufferedReader input = new BufferedReader(new InputStreamReader(predictorSocket.getInputStream()));
		
		String imgURL= "URL of the img";
		// Send command
		output.printf("%s", MainController.FEED_COMMAND);
		output.flush();
		output.printf("%s\n", imgURL);
		output.flush();
		// Get ack
		String ack = input.readLine();
		if (ack.equals("Received " + imgURL)) res = false;
		// Close the connection
		predictorSocket.close();
		// Return op result
		return res;
	}
}
