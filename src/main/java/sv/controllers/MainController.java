package sv.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Date;

import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import net.lod4all.api.LOD4All;
import sv.entities.ClientFilterMessage;

@Controller
@PropertySource(value = { "classpath:network.properties" })
public class MainController {
	
	private static final Logger logger = LoggerFactory.getLogger(MainController.class);
	
	@Value("${mls.host}")
	public String MLS_HOST;
	@Value("${mls.port}")
	public int MLS_PORT;
	
	@Autowired
	private SimpMessagingTemplate template;
	
	private static final String ASK_COMMAND = "AS";
	private static final String FEED_COMMAND = "FE";
	// Each FEED_FREQ minutes the web server will provide new images to the predictor
	private static final long FEED_FREQ = 5;
	
	private static final String NOTIFICATION_ALERT_COLOR = "#ff0000";
	private static final String NOTIFICATION_OK_COLER = "#0000ff";
	private static final String NOTIFICATION_ALERT_MSG = "ALERT";
	private static final String NOTIFICATION_OK_MSG = "OK" ;
	
	private static final String CAMERA_CUATRO_CAMINOS = "Cuatro caminos";
	private static final String CUATRO_CAMINOS_GRAPH = "http://datos.gob.es/catalogo/camara-de-cuatro-caminos";
	
	private static final String WS_SUBSCRIPTION_ENDPOINT_CUATRO_CAMINOS = "/cam_cuatro_caminos";
	
	private static final long L4A_LAST_EPOCH = 1456355446354L;
	private static final long L4A_FIRST_EPOCH = 1456472692208L;
	
	public long getSimulatedEpoch(long currentEpoch){
		currentEpoch = currentEpoch%(L4A_LAST_EPOCH-L4A_FIRST_EPOCH);
		currentEpoch += L4A_FIRST_EPOCH;
		return currentEpoch;
	}
	
	public JSONArray getLatestImg(Date date){
		// LOD4ALL data is only between 24feb and 26feb, so it will be simulated
		long epoch = getSimulatedEpoch(date.getTime());
		
		// initialize LOD4ALL API
		LOD4All lod4All = LOD4All.initialize("xawsaykmcb");
		
		// execute query
		String query = "SELECT DISTINCT * WHERE { GRAPH <"+CUATRO_CAMINOS_GRAPH+"> { ?s dc:last_modified ?lm . FILTER(xsd:integer(?lm) <= "+epoch+") } }"
				+ "ORDER BY DESC(?lm) LIMIT 1";
		lod4All.query(query).showQuery();
		JSONArray result = lod4All.runQuery2Json(false);
		
		return result;
	}
	
	public JSONArray getLatestImg(long currEpoch){
		// LOD4ALL data is only between 24feb and 26feb, so it will be simulated
		long epoch = getSimulatedEpoch(currEpoch);
		
		// initialize LOD4ALL API
		LOD4All lod4All = LOD4All.initialize("xawsaykmcb");
		
		// execute query
		String query = "SELECT DISTINCT * WHERE { GRAPH <"+CUATRO_CAMINOS_GRAPH+"> { ?s dc:last_modified ?lm . FILTER(xsd:integer(?lm) <= "+epoch+") } }"
				+ "ORDER BY DESC(?lm) LIMIT 1";
		lod4All.query(query).showQuery();
		JSONArray result = lod4All.runQuery2Json(false);
		
		return result;
	}
	
	/**
	 * Returns the latest image source from a camera
	 * @param camera Camera string
	 * @throws Exception 
	 */
	@RequestMapping(value = "/get_data", method = RequestMethod.GET)
	public ResponseEntity<?> get_data(@RequestParam(value = "camera", required = true) String camera,
			@RequestParam(value = "date", required = true) @DateTimeFormat(pattern="yyyy-MM-dd_hh:mm:ss") Date date) throws Exception{
		JSONArray res = getLatestImg(date);
		return null;
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
		long epoch = getSimulatedEpoch(System.currentTimeMillis());
		
		Socket predictorSocket = new Socket(MLS_HOST, MLS_PORT);
		logger.info("Connected to the predictor");
		PrintWriter output = new PrintWriter(predictorSocket.getOutputStream(), false);
		BufferedReader input = new BufferedReader(new InputStreamReader(predictorSocket.getInputStream()));
		
		JSONArray queryRes = getLatestImg(System.currentTimeMillis());
		String imgURL = "URL of the img";
		// Send command
		output.printf("%s", MainController.FEED_COMMAND);
		output.flush();
		output.printf("%s\n", imgURL);
		output.flush();
		// Get ack
		String ack = input.readLine();
		if (ack.equals("ALERT")) res = false;
		// Close the connection
		predictorSocket.close();
		
		// Cam did not detect anything
		if (res) this.template.convertAndSend(WS_SUBSCRIPTION_ENDPOINT_CUATRO_CAMINOS, NOTIFICATION_ALERT_MSG);
		else this.template.convertAndSend(WS_SUBSCRIPTION_ENDPOINT_CUATRO_CAMINOS, NOTIFICATION_OK_MSG);
		
		// Return op result
		return res;
	}
}
