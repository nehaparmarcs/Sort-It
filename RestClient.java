import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import javax.ws.rs.core.MediaType;



public class RestClient extends Actor {

      public void act() 
    {
        // Add your action code here.
    }    
    
  public void getQuiz(String key) {
    try {

        Client client = Client.create();

        WebResource webResource = client
           .resource("http://35.160.219.130:8080/location/ms/rest/getgame/"+key);

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
           .resource("http://35.160.219.130:8080/location/ms/rest/postgame");

        String input = "{\"gameName\":\""   +gameName+"\",\"playerName\":\""+playerName+"\"}";
        String input1 = "{\"gameName\":\"Game123\",\"playerName\":\"Neha\"}";
         System.out.println(input1);
        ClientResponse response = webResource.type("application/json")
           .post(ClientResponse.class, input1);

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
    
    
    
}

