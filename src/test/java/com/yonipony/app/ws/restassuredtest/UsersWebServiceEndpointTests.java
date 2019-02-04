package com.yonipony.app.ws.restassuredtest;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.restassured.RestAssured;
//import static io.restassured.RestAssured.given;
import io.restassured.response.Response;

class UsersWebServiceEndpointTests {

	private static final String APPLICATION_JSON = "application/json";
	private static final String CONTEXT_PATH = "/yonipony";
	private static final String EMAIL_ADDRESS = "sergey.kargopolov@swiftdeveloperblog.com";
	private static final String HEADER_AUTHORIZATION_VALUE = "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJwb3BvMTIiLCJleHAiOjE1NDk3Mzc3Nzh9._rFDi81AYMtBIkS-kGu5vkBxr3uZcvxQFcSKBRyOwxvthZpRDjxppp_HCq0gZqUNlUPlwy_zBixaK5MQVyv65w";
	private static String authorizationHeader;
	private static String userId = "Iz54P9ipb9pCryqiyuHUpXqrbfHB30";

	List<Map<String, String>> resAddresses;

	@BeforeEach
	void setUp() throws Exception {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = 8080;
	}

//	@Test
//	void testUserLogin() {
//		Map<String, String> loginDetails = new HashMap<>();
//		loginDetails.put("email", EMAIL_ADDRESS);
//		loginDetails.put("password", "123");
//
//		Response response = given().contentType(APPLICATION_JSON).body(loginDetails).when()
//				.post(CONTEXT_PATH + "/login").then().statusCode(200).contentType(APPLICATION_JSON).extract()
//				.response();
//
//		assertNotNull(response);
//
//		authorizationHeader = response.getHeader("Authorization");
//		userId = response.getHeader("UserId");
//	}

	@Test
	final void testGetUser() {
		Response response = given().header("Authorization", HEADER_AUTHORIZATION_VALUE).pathParam("userId", userId)
				.when().get(CONTEXT_PATH + "/users/{userId}").then().statusCode(200).contentType(APPLICATION_JSON)
				.extract().response();

		String resUserId = response.jsonPath().getString("userId");
		String resEmail = response.jsonPath().getString("email");
		resAddresses = response.jsonPath().getList("addresses");

		assertNotNull(resUserId);
		assertEquals(resEmail, EMAIL_ADDRESS);
		assertNotNull(resAddresses);
		assertEquals(resAddresses.size(), 2);
		String bodyString = response.body().asString();
		try {
			JSONObject responseBodyJson = new JSONObject(bodyString);
			JSONArray addressesJson = responseBodyJson.getJSONArray("addresses");

			assertNotNull(addressesJson);
			assertTrue(addressesJson.length() == 2);

			String addressId = addressesJson.getJSONObject(0).getString("addressId");

			assertNotNull(addressId);
			assertTrue(addressId.length() == 30);
		} catch (JSONException e) {
			fail(e.getMessage());
		}
	}

	@Test
	final void testUpdateUser() {
		Map<String, String> userDetails = new HashMap<>();
		userDetails.put("firstName", "popo");
		userDetails.put("lastName", "yoyo");

		Response response = given().contentType(APPLICATION_JSON).header("Authorization", HEADER_AUTHORIZATION_VALUE)
				.pathParam("userId", userId).accept(APPLICATION_JSON).body(userDetails).when()
				.put(CONTEXT_PATH + "/users/{userId}").then().statusCode(200).contentType(APPLICATION_JSON).extract()
				.response();

		String resFirstName = response.jsonPath().getString("firstName");
		String resLastName = response.jsonPath().getString("lastName");
		List<Map<String, String>> resUpdatedAddresses = response.jsonPath().getList("addresses");

		assertNotNull(resFirstName);
		assertNotNull(resLastName);
		assertEquals(resFirstName, "popo");
		assertEquals(resLastName, "yoyo");
		assertNotNull(resUpdatedAddresses);
		assertEquals(resUpdatedAddresses.size(), 2);
		//assertEquals(resUpdatedAddresses.get(0).toString(), resAddresses.get(0).toString());
	}

}
