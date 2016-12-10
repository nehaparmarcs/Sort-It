import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import javax.ws.rs.core.MediaType;

import java.io.IOException;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;


public class RestClient extends Actor {

      public void act() 
    {
        // Add your action code here.
    }    
    
  public void getQuiz(String key) {
    try {

        Client client = Client.create();

        WebResource webResource = client
           .resource("http://localhost:8080/location/ms/rest/getgame/"+key);

        ClientResponse response = webResource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);

        if (response.getStatus() != 201) {
           throw new RuntimeException("Failed : HTTP error code : "
            + response.getStatus());
        }

        String output = response.getEntity(String.class);

        System.out.println("Output from Server .... \n");
        System.out.println(output);

      } catch (Exception e) {

        e.printStackTrace();

      }

    }
    
    
    public void postData(String gameName, String playerName) {

    try {

        Client client = Client.create();

        WebResource webResource = client
           .resource("http://localhost:8080/location/ms/rest/postgame");

        String input = "{\"gameName\":\""   +gameName+"\",\"playerName\":\""+playerName+"\"}";
        //String input1 = "{\"gameName\":\"Game123\",\"playerName\":\"Neha\"}";
         System.out.println(input);
        ClientResponse response = webResource.type("application/json")
           .post(ClientResponse.class, input);

        if (response.getStatus() != 201) {
            throw new RuntimeException("Failed : HTTP error code : "
                 + response.getStatus());
        }

        System.out.println("Output from Server .... \n");
        String output = response.getEntity(String.class);
        System.out.println(output);

     } catch (Exception e) {
        e.printStackTrace();
     }

}



    
	public Game getCurrentGame(String gameID){
		
		Client client = Client.create();
		
		WebResource webResource = client
				.resource("http://localhost:8080/location/ms/rest/getgame/"+gameID);
		
		ClientResponse response = webResource.type("application/json")
				.get(ClientResponse.class);

		if(response.getStatus() != 201) {
			throw new RuntimeException("Failed : HTTP error code : "
					+ response.getStatus());
		}

		ObjectMapper mapper = new ObjectMapper();
		
		GamesList game = new GamesList();
		
		try {
			game = mapper.readValue(response.getEntity(String.class), GamesList.class);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientHandlerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UniformInterfaceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Current Game "+ game.getGame()[0]);
		
		return game.getGame()[0];
		
	}

    
}

