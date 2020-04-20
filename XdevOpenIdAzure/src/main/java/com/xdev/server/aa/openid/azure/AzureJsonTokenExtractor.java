package com.xdev.server.aa.openid.azure;

import java.io.IOException;
import java.net.URI;
import java.text.ParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.scribejava.core.exceptions.OAuthException;
import com.github.scribejava.core.extractors.OAuth2AccessTokenJsonExtractor;
import com.github.scribejava.core.model.OAuth2AccessTokenErrorResponse;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.utils.Preconditions;
import com.google.gson.JsonObject;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import com.xdev.server.aa.openid.helper.JsonHelper;

public class AzureJsonTokenExtractor extends OAuth2AccessTokenJsonExtractor {

	private static final Logger log = LoggerFactory.getLogger(AzureJsonTokenExtractor.class);

	private static final String ACCESS_TOKEN_KEY = "access_token";
	private static final String TOKEN_TYPE_KEY = "token_type";
	private static final String EXPIRES_IN_KEY = "expires_in";
	private static final String REFRESH_TOKEN_KEY = "refresh_token";
	private static final String ID_TOKEN_KEY = "id_token";
	private static final String SCOPE_KEY = "scope";
	private static final String ERROR_KEY = "error";
	private static final String ERROR_DESCRIPTION_KEY = "error_description";
	private static final String ERROR_URI_KEY = "error_uri";

	protected AzureJsonTokenExtractor() {
	}

	private static class InstanceHolder {

		private static final AzureJsonTokenExtractor INSTANCE = new AzureJsonTokenExtractor();

	}

	public static AzureJsonTokenExtractor instance() {
		return InstanceHolder.INSTANCE;
	}

	@Override
	public AzureToken extract(Response response) throws IOException, OAuthException {

		final String body = response.getBody();
		Preconditions.checkEmptyString(body, "Response body is incorrect. Can't extract a token from an empty string");

		if (response.getCode() != 200) {

			log.error("Request returns with HTTP status code " + response.getCode());

			generateError(response.getBody());
		}
		return createToken(response.getBody());

	}

	/**
	 * Related documentation: https://tools.ietf.org/html/rfc6749#section-5.2
	 */
	protected static void generateError(String response) {
		JsonObject obj;
		try {
			obj = JsonHelper.parseJSON(response);
		} catch (Exception e) {
			throw new OAuthException("Response body can't parsed as json", e);
		}

		final String errorInString = extractParameter(obj, ERROR_KEY, true);
		final String errorDescription = extractParameter(obj, ERROR_DESCRIPTION_KEY, false);
		final String errorUriInString = extractParameter(obj, ERROR_URI_KEY, false);
		URI errorUri;
		try {
			errorUri = errorUriInString == null ? null : URI.create(errorUriInString);
		} catch (IllegalArgumentException iae) {
			errorUri = null;
		}

		throw new OAuth2AccessTokenErrorResponse(OAuth2AccessTokenErrorResponse.ErrorCode.valueOf(errorInString),
				errorDescription, errorUri, response);
	}

	protected static String extractParameter(JsonObject jsonresponse, String key, boolean required)
			throws OAuthException {

		if (jsonresponse.get(key) != null) {
			return jsonresponse.get(key).getAsString();
		}

		if (required) {
			throw new OAuthException("Response body is incorrect. Can't extract a '" + key + "' from this: '"
					+ jsonresponse.toString() + "'", null);
		}

		return null;
	}

	private AzureToken createToken(String response) throws OAuthException {
		try {
			JsonObject json = JsonHelper.parseJSON(response);

			final String accessToken = extractParameter(json, ACCESS_TOKEN_KEY, false);
			final String tokenType = extractParameter(json, TOKEN_TYPE_KEY, false);
			final String expiresInString = extractParameter(json, EXPIRES_IN_KEY, false);
			Integer expiresIn;
			try {
				expiresIn = expiresInString == null ? null : Integer.valueOf(expiresInString);
			} catch (NumberFormatException nfe) {
				expiresIn = null;
			}
			final String refreshToken = extractParameter(json, REFRESH_TOKEN_KEY, false);
			final String scope = extractParameter(json, SCOPE_KEY, false);

			// ID Token
			JWT idToken;

			idToken = JWTParser.parse(extractParameter(json, ID_TOKEN_KEY, true));

			return createToken(accessToken, tokenType, expiresIn, refreshToken, scope, response, idToken);

		} catch (ParseException e) {
			throw new OAuthException("Could not extract id token content.", e);
		} catch (Exception e) {
			throw new OAuthException("Token creation failed.", e);
		}

	}

	protected AzureToken createToken(String accessToken, String tokenType, Integer expiresIn, String refreshToken,
			String scope, String rawResponse, JWT idToken) {
		return new AzureToken(accessToken, tokenType, expiresIn, refreshToken, scope, rawResponse, idToken);
	}

}
