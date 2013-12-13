package rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import cache_controller.CacheController;

@Path("/")
public class CacheWebService {
	
	private static final String EMPTY_JSON = "{\"t\":[],\"f\":[],\"r\":[]}";
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String processRequest(@QueryParam("lat") String lat, @QueryParam("lng") String lng,
			@QueryParam("rad") String rad, @QueryParam("olat") String olat, @QueryParam("olng") String olng,
			@QueryParam("orad") String orad, @QueryParam("filter") String filters) throws Exception {
		
		// Check parameters
		if (lat == null || lng == null || rad == null || filters == null) {
			return EMPTY_JSON;
		}
		
		// Correct for null original input
		if (olat == null || olng == null || orad == null) {
			olat = "0";
			olng = "0";
			orad = "0";
		}
		
		// TODO Check valid values for params
		
		// Get results from cache
		String results = CacheController.getResults(lat, lng, rad, olat, olng, orad, filters);
		return results;
	}
	
	@GET
	@Path("downvote")
	@Produces(MediaType.TEXT_PLAIN)
	public String downvote(@QueryParam("filter") String filter, @QueryParam("lat") String lat, @QueryParam("lng") String lng,
			@QueryParam("id") String id) {
		
		// Check parameters
		if (lat == null || lng == null || id == null || filter == null) {
			return "";
		}
		
		// TODO Check valid values for params
		
		// Down vote or erase
		int newRep = CacheController.downvote(filter, lat, lng, id);
		
		switch (newRep) {
			case -1: return "Error: Could not read from cache";
			case -2: return "Error: Record not found in cache";
			case -3: return "Error: Could not update/delete tweet";
			default: return "New reputation: " + newRep;
		}
	}
}