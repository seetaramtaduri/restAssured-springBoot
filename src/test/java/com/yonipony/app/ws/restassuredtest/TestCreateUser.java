package com.yonipony.app.ws.restassuredtest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.restassured.RestAssured;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

class TestCreateUser {

	private static final String APPLICATION_JSON = "application/json";
	private static final String CONTEXT_PATH = "/yonipony";

	@BeforeEach
	void setUp() throws Exception {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = 8080;
	}

	@Test
	void testCreateUser() {
		List<Map<String, Object>> userAddresses = new ArrayList<>();

		Map<String, Object> shippingAddress = new HashMap<>();
		shippingAddress.put("city", "Vancouver");
		shippingAddress.put("country", "Canada");
		shippingAddress.put("streetName", "123 Street name");
		shippingAddress.put("postalCode", "123456");
		shippingAddress.put("type", "shipping");

		Map<String, Object> billingAddress = new HashMap<>();
		billingAddress.put("city", "Vancouver");
		billingAddress.put("country", "Canada");
		billingAddress.put("streetName", "123 Street name");
		billingAddress.put("postalCode", "123456");
		billingAddress.put("type", "billing");

		userAddresses.add(shippingAddress);
		userAddresses.add(billingAddress);

		Map<String, Object> userDetails = new HashMap<>();
		userDetails.put("firstName", "Sergey");
		userDetails.put("lastName", "Kargopolov");
		userDetails.put("email", "sergey.kargopolov@swiftdeveloperblog.com");
		userDetails.put("password", "123");
		userDetails.put("addresses", userAddresses);

		Response response = given().contentType(APPLICATION_JSON).accept(APPLICATION_JSON).body(userDetails).when()
				.post(CONTEXT_PATH + "/users").then().statusCode(200).contentType(APPLICATION_JSON).extract()
				.response();

		String userId = response.jsonPath().getString("userId");
		assertNotNull(response);
		assertNotNull(userId);
		assertTrue(userId.length() == 30);

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

}
