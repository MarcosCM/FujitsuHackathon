package sv.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.FileManager;
import org.apache.jena.query.*;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/*import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.util.FileManager;*/

@Controller
@PropertySource(value = { "classpath:network.properties" })
public class MainController {
	
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
	private static final String NOTIFICATION_OK_MSG = "OK";
	
	private static final String CAMERA_CUATRO_CAMINOS = "Cuatro caminos";
	private static final String CUATRO_CAMINOS_GRAPH = "http://datos.gob.es/catalogo/camara-de-cuatro-caminos";
	private static final String CUATRO_CAMINOS_URI = "http://datos.santander.es/api/rest/datasets/camara_cuatro_caminos.rdf";
	
	private static final String WS_SUBSCRIPTION_ENDPOINT_CUATRO_CAMINOS = "/cam_cuatro_caminos";
	
	private static final long L4A_LAST_EPOCH = 1456355446354L;
	private static final long L4A_FIRST_EPOCH = 1456472692208L;
	/*
	public long getSimulatedEpoch(long currentEpoch){
		currentEpoch = currentEpoch%(L4A_LAST_EPOCH-L4A_FIRST_EPOCH);
		currentEpoch += L4A_FIRST_EPOCH;
		return currentEpoch;
	}*/
	
	public JSONArray getLatestImg(long currEpoch){
		Model model = FileManager.get().loadModel(CUATRO_CAMINOS_URI);
		
		String queryStr = "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> "
				+ "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
				+ "SELECT DISTINCT ?s ?p ?o WHERE { GRAPH <"+CUATRO_CAMINOS_GRAPH+"> { ?s ?p ?o . ?s dc:modified ?lm . FILTER(xsd:integer(?lm) <= "+currEpoch+") } } "
				+ "ORDER BY DESC(?lm) LIMIT 1";
		
		Query query = QueryFactory.create(queryStr);
        QueryExecution qexec = QueryExecutionFactory.create(query, model);
        try {
        	System.out.println("ASDASDASD");
            ResultSet results = qexec.execSelect();
            System.out.println("asdasdasd --" + results.getRowNumber());
            for (; results.hasNext();) {
                String sentencia = "";
                System.out.println("fjdjfdjfjf");
                QuerySolution soln = results.nextSolution();
                System.out.println("mgmgsmdg");
                Resource x = soln.getResource("s");
                System.out.println("12312123");
                sentencia += x.getURI() + " - ";
                System.out.println(sentencia);
                x = soln.getResource("p");
                sentencia += x.getURI() + " - ";
                
                x = soln.getResource("o");
                sentencia += x.getURI() + " - ";
                
                System.out.println(sentencia);
            }
        } finally {
            qexec.close();
        }
		
		return null;
		/*
		// LOD4ALL data is only between 24feb and 26feb, so it will be simulated
		long epoch = getSimulatedEpoch(currEpoch);
		
		// initialize LOD4ALL API
		LOD4All lod4All = LOD4All.initialize("xawsaykmcb");
		
		System.out.println(epoch);
		// execute query
		String query = "SELECT DISTINCT * WHERE { GRAPH <"+CUATRO_CAMINOS_GRAPH+"> { ?s dc:modified ?lm . FILTER(xsd:integer(?lm) <= "+epoch+") } } "
				+ "ORDER BY DESC(?lm) LIMIT 1";
		lod4All.query(query).showQuery();
		JSONArray result = lod4All.runQuery2Json(false);
		
		return result;*/
	}
	
	public JSONArray getLatestImg(Date date){
		return getLatestImg(date.getTime());
	}
	
	/**
	 * Returns the latest image source from a camera
	 * @param camera Camera string
	 * @throws Exception 
	 */
	@RequestMapping(value = "/get_data", method = RequestMethod.GET)
	public ResponseEntity<?> get_data(@RequestParam(value = "camera", required = true) String camera,
			@RequestParam(value = "date", required = true) String date) throws Exception{
		int year = Integer.parseInt(date.substring(0, 4));
		int month = Integer.parseInt(date.substring(5, 7));
		int day = Integer.parseInt(date.substring(8, 10));
		int hour = Integer.parseInt(date.substring(11,13));
		int min = Integer.parseInt(date.substring(14,16));
		int sec = Integer.parseInt(date.substring(17,19));
		System.out.printf("%d %d %d %d %d %d\n", year, month, day, hour, min, sec);
		
		JSONArray res = getLatestImg(new Date(year, month, day, hour, min, sec));
		System.out.println(res);
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
		long epoch = System.currentTimeMillis();
		
		Socket predictorSocket = new Socket(MLS_HOST, MLS_PORT);
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
		if (ack.equals(NOTIFICATION_ALERT_MSG)) res = false;
		// Close the connection
		predictorSocket.close();
		
		// Cam did not detect anything
		if (res) this.template.convertAndSend(WS_SUBSCRIPTION_ENDPOINT_CUATRO_CAMINOS, NOTIFICATION_ALERT_MSG);
		else this.template.convertAndSend(WS_SUBSCRIPTION_ENDPOINT_CUATRO_CAMINOS, NOTIFICATION_OK_MSG);
		
		// Return op result
		return res;
	}
}
