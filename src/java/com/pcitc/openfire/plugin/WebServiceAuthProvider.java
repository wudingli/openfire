package com.pcitc.openfire.plugin;

import java.util.HashMap;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;
import org.dom4j.XPath;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.auth.AuthProvider;
import org.jivesoftware.openfire.auth.ConnectionException;
import org.jivesoftware.openfire.auth.InternalUnauthenticatedException;
import org.jivesoftware.openfire.auth.UnauthorizedException;
import org.jivesoftware.openfire.user.UserAlreadyExistsException;
import org.jivesoftware.openfire.user.UserManager;
import org.jivesoftware.openfire.user.UserNotFoundException;
import org.jivesoftware.util.JiveGlobals;
import org.jivesoftware.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebServiceAuthProvider implements AuthProvider {
	
	
	private static final Logger Log = LoggerFactory.getLogger(WebServiceAuthProvider.class);

	private String wsAddress;
	private String authMethodSOAPRequest;
	private String authMethodContentType;
	@SuppressWarnings("unused")
	private PasswordType passwordType;
	private String flagField;
	private String usernameField;
	private String passwordField;
	private String nameField;
	private String emailField;
//	private String namespace;
//	
//	private HashMap xmlMap = new HashMap(); 
	//private WebServiceCaller wsCaller = new WebServiceCaller();

	public WebServiceAuthProvider() {
        JiveGlobals.migrateProperty("webServiceAuthProvider.http");
        JiveGlobals.migrateProperty("webServiceAuthProvider.authMethodSOAPRequest");
        JiveGlobals.migrateProperty("webServiceAuthProvider.authMethodContentType");
        JiveGlobals.migrateProperty("webServiceAuthProvider.passwordType");
        JiveGlobals.migrateProperty("webServiceAuthProvider.flagField");
        JiveGlobals.migrateProperty("webServiceAuthProvider.usernameField");
        JiveGlobals.migrateProperty("webServiceAuthProvider.passwordField");
        JiveGlobals.migrateProperty("webServiceAuthProvider.nameField");
        JiveGlobals.migrateProperty("webServiceAuthProvider.emailField");
        //JiveGlobals.migrateProperty("webServiceAuthProvider.namespace");
        wsAddress = JiveGlobals.getProperty("webServiceAuthProvider.http");
        authMethodSOAPRequest = JiveGlobals.getProperty("webServiceAuthProvider.authMethodSOAPRequest");
        authMethodContentType = JiveGlobals.getProperty("webServiceAuthProvider.authMethodContentType");
        flagField = JiveGlobals.getProperty("webServiceAuthProvider.flagField");
        usernameField = JiveGlobals.getProperty("webServiceAuthProvider.usernameField");
        passwordField = JiveGlobals.getProperty("webServiceAuthProvider.passwordField");
        nameField = JiveGlobals.getProperty("webServiceAuthProvider.nameField");
        emailField = JiveGlobals.getProperty("webServiceAuthProvider.emailField");
        //namespace = JiveGlobals.getProperty("webServiceAuthProvider.namespace", "http://tempuri.org/");
        passwordType = PasswordType.plain;
        try {
            passwordType = PasswordType.valueOf(
                    JiveGlobals.getProperty("webServiceAuthProvider.passwordType", "plain"));
        }
        catch (IllegalArgumentException iae) {
            Log.error(iae.getMessage(), iae);
        }
	}

	@Override
	public boolean isPlainSupported() {
		return true;
	}

	@Override
	public boolean isDigestSupported() {
		return false;
	}

	@Override
	public void authenticate(String username, String password)
			throws UnauthorizedException, ConnectionException,
			InternalUnauthenticatedException {
		
		if (username == null || password == null) {
            throw new UnauthorizedException();
        }
        username = username.trim().toLowerCase();
        if (username.contains("@")) {
            // Check that the specified domain matches the server's domain
            int index = username.indexOf("@");
            String domain = username.substring(index + 1);
            if (domain.equals(XMPPServer.getInstance().getServerInfo().getXMPPDomain())) {
                username = username.substring(0, index);
            } else {
                // Unknown domain. Return authentication failed.
                throw new UnauthorizedException();
            }
        }
        
        String name = null;
		String email = null;
		
		try{
			String soapRequest = authMethodSOAPRequest;
			Document docRequest = DocumentHelper.parseText(soapRequest);
			
			String namespace = WebServiceCaller.getNamespaceFromSOAPRequest(docRequest);
			HashMap<String, String> xmlMap = new HashMap<String, String>();
			xmlMap.put("requestns",namespace);
			
			XPath xRequest = docRequest.createXPath("//requestns:"+usernameField);
			xRequest.setNamespaceURIs(xmlMap);
            Node node = xRequest.selectSingleNode(docRequest);
            node.setText(username);
            xRequest = docRequest.createXPath("//requestns:"+passwordField);
            xRequest.setNamespaceURIs(xmlMap);
            node = xRequest.selectSingleNode(docRequest);
            node.setText(password);
            
            soapRequest = docRequest.asXML();
            
			String authResult = WebServiceCaller.callWebService(wsAddress, soapRequest, authMethodContentType);
			Document doc = DocumentHelper.parseText(authResult);
			
			namespace = WebServiceCaller.getNamespaceFromSOAPRequest(doc);
			xmlMap.put("resultns",namespace);
			
			XPath x = doc.createXPath("//resultns:"+flagField);
			x.setNamespaceURIs(xmlMap);
			Node no = x.selectSingleNode(doc);
			String flag = no.getText();
			if(!"true".equals(flag.toLowerCase())){
				throw new UnauthorizedException();
			}
			
			x = doc.createXPath("//resultns:"+nameField);
			x.setNamespaceURIs(xmlMap);
			no = x.selectSingleNode(doc);
			if(no != null){
				name = no.getText();
			}
			
			x = doc.createXPath("//resultns:"+emailField);
			x.setNamespaceURIs(xmlMap);
			no = x.selectSingleNode(doc);
			if(no != null){
				email = no.getText();
			}
			/*String regexp = ".+<"+flagName+">(.+)</"+flagName+">.+"; 
			Pattern pattern = Pattern.compile(regexp);
			Matcher matcher = pattern.matcher(authResult);
			String flag = matcher.group(1);
			if(flag.toLowerCase() != "true"){
				throw new UnauthorizedException();
			}
			regexp = ".+<"+userNameName+">(.+)</"+userNameName+">.+";
			pattern = Pattern.compile(regexp);
			matcher = pattern.matcher(authResult);
			String name = null;
			if(matcher.matches()){
				name = matcher.group(1);
			}
			regexp = ".+<"+userEmailName+">(.+)</"+userEmailName+">.+";
			pattern = Pattern.compile(regexp);
			matcher = pattern.matcher(authResult);
			String email = null;
			if(matcher.matches()){
				email = matcher.group(1);
			}		*/	
			
		}catch(Exception e){
			throw new UnauthorizedException();
		}
		createUser(username, name, email);
	}



	@Override
	public void authenticate(String username, String token, String digest)
			throws UnauthorizedException, ConnectionException,
			InternalUnauthenticatedException {
		throw new UnsupportedOperationException("Digest authentication not supported by WebService authentication mode for now.");
	}

	@Override
	public String getPassword(String username) throws UserNotFoundException,
			UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setPassword(String username, String password)
			throws UserNotFoundException, UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean supportsPasswordRetrieval() {
		return false;
	}
	
    /**
     * Checks to see if the user exists; if not, a new user is created.
     *
     * @param username the username.
     * @param name 
     * @param email 
     */
    private static void createUser(String username, String name, String email) {
        // See if the user exists in the database. If not, automatically create them.
        UserManager userManager = UserManager.getInstance();
        try {
            userManager.getUser(username);
        }
        catch (UserNotFoundException unfe) {
            try {
                Log.debug("WebServiceAuthProvider: Automatically creating new user account for " + username);
                UserManager.getUserProvider().createUser(username, StringUtils.randomString(8),
                        name, email);
            }
            catch (UserAlreadyExistsException uaee) {
                // Ignore.
            }
        }
    }
    
    public enum PasswordType {
	    /**
         * The password is stored as plain text.
         */
        plain,

        /**
         * The password is stored as a hex-encoded MD5 hash.
         */
        md5,

        /**
         * The password is stored as a hex-encoded SHA-1 hash.
         */
        sha1,
        
        /**
         * The password is stored as a hex-encoded SHA-256 hash.
         */
        sha256,
              
        /**
          * The password is stored as a hex-encoded SHA-512 hash.
          */
        sha512;
	}
}
