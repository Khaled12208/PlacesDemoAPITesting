package APIPackage.APITesting;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import static io.restassured.RestAssured.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.testng.Assert;

public class PlaceCategory {
	
	String BodyResponseData;
	Response Resp;
	JsonPath JsPath;
	int NumberOfNearbyPlaces;
	Map<String, String> params = new HashMap<String, String>();
	
	@BeforeTest
	public void GetResponseBody()
	{
		RestAssured.baseURI= "https://places.demo.api.here.com";
		Resp = given()
				.queryParam("app_code", "AJKnXv84fjrb0KIHawS0Tg")
				.queryParam("app_id", "DemoAppId01082013GAL")
				.queryParam("at", "52.521,13.3807")
				.queryParam("pretty", "true")
				.header("Accept","application/json")
				.header("Accept-Encoding","gzip")
				.when().get("/places/v1/categories/places")
		.then().assertThat().statusCode(200).extract().response();
		 BodyResponseData= Resp.asString();
		 JsPath = new JsonPath(BodyResponseData);			
		
	}
	@Test
	public void ValidateReponseCode()
	{
		// Validate that the repose code is 200
		Assert.assertEquals(Resp.getStatusCode(), 200);

	}
	
	@Test
	public void ValidateReponseTime()
	{
		// Validate that the response time is not more than 2000ms
		Assert.assertTrue(Resp.getStatusCode() <= 2000);

	}
	
	@Test
	public void validateNumberOfPlaces()
	{
		// Validate that the Number Of Recommended places not less than 80 
		NumberOfNearbyPlaces = JsPath.getInt("items.size()");
		Assert.assertTrue(NumberOfNearbyPlaces >= 80);
		
	}
	
	@Test(dependsOnMethods = { "validateNumberOfPlaces" })
	public void ValidateHostelCategoryIsExist()
	{

		ArrayList<String> ExistantCategoryList = new ArrayList <String>();
		//Extracts The Existent Places		
		for (int i=0; i<NumberOfNearbyPlaces; i++)
		{
			ExistantCategoryList.add(JsPath.getString("items["+i+"].id"));			
		}
		
		Assert.assertTrue(ExistantCategoryList.contains("hostel"));


		
	}
	
}