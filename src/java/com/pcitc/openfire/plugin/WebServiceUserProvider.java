package com.pcitc.openfire.plugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;
import org.dom4j.XPath;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.user.User;
import org.jivesoftware.openfire.user.UserAlreadyExistsException;
import org.jivesoftware.openfire.user.UserCollection;
import org.jivesoftware.openfire.user.UserNotFoundException;
import org.jivesoftware.openfire.user.UserProvider;
import org.jivesoftware.util.JiveGlobals;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.packet.JID;


public class WebServiceUserProvider implements UserProvider {
	
	private static final Logger Log = LoggerFactory.getLogger(WebServiceUserProvider.class);
	
	private String wsAddress;
	private String loadUserMethodSOAPRequest;
	private String loadUserMethodContentType;
	private String userCountMethodSOAPRequest;
	private String userCountMethodContentType;
	private String allUsersMethodSOAPRequest;
	private String allUsersMethodContentType;
	private String searchMethodSOAPRequest;
	private String searchMethodContentType;
	
	private String usernameField;
	private String nameField;
	private String emailField;
	private String userCountField;
//	private String namespace;
//	
//	private HashMap xmlMap = new HashMap();
	
	//private WebServiceCaller wsCaller = new WebServiceCaller();
	
	private static final boolean IS_READ_ONLY = true;
	
	public WebServiceUserProvider() {
		JiveGlobals.migrateProperty("webServiceUserProvider.http");
        JiveGlobals.migrateProperty("webServiceUserProvider.loadUserMethodSOAPRequest");
        JiveGlobals.migrateProperty("webServiceUserProvider.loadUserMethodContentType");
        JiveGlobals.migrateProperty("webServiceUserProvider.userCountMethodSOAPRequest");
        JiveGlobals.migrateProperty("webServiceUserProvider.userCountMethodContentType");
        JiveGlobals.migrateProperty("webServiceUserProvider.allUsersMethodSOAPRequest");
        JiveGlobals.migrateProperty("webServiceUserProvider.allUsersMethodContentType");
        JiveGlobals.migrateProperty("webServiceUserProvider.searchMethodSOAPRequest");
        JiveGlobals.migrateProperty("webServiceUserProvider.searchMethodContentType");
        JiveGlobals.migrateProperty("webServiceUserProvider.usernameField");
        JiveGlobals.migrateProperty("webServiceUserProvider.nameField");
        JiveGlobals.migrateProperty("webServiceUserProvider.emailField");
        JiveGlobals.migrateProperty("webServiceUserProvider.userCountField");
        //JiveGlobals.migrateProperty("webServiceUserProvider.namespace");
        
        wsAddress = JiveGlobals.getProperty("webServiceUserProvider.http");
        loadUserMethodSOAPRequest = JiveGlobals.getProperty("webServiceUserProvider.loadUserMethodSOAPRequest");
        loadUserMethodContentType = JiveGlobals.getProperty("webServiceUserProvider.loadUserMethodContentType");
        userCountMethodSOAPRequest = JiveGlobals.getProperty("webServiceUserProvider.userCountMethodSOAPRequest");
        userCountMethodContentType = JiveGlobals.getProperty("webServiceUserProvider.userCountMethodContentType");
        allUsersMethodSOAPRequest = JiveGlobals.getProperty("webServiceUserProvider.allUsersMethodSOAPRequest");
        allUsersMethodContentType = JiveGlobals.getProperty("webServiceUserProvider.allUsersMethodContentType");
        searchMethodSOAPRequest = JiveGlobals.getProperty("webServiceUserProvider.searchMethodSOAPRequest");
        searchMethodContentType = JiveGlobals.getProperty("webServiceUserProvider.searchMethodContentType");
        
        usernameField = JiveGlobals.getProperty("webServiceUserProvider.usernameField");
        nameField = JiveGlobals.getProperty("webServiceUserProvider.nameField");
        emailField = JiveGlobals.getProperty("webServiceUserProvider.emailField");
        userCountField = JiveGlobals.getProperty("webServiceUserProvider.userCountField");
//        namespace = JiveGlobals.getProperty("webServiceUserProvider.namespace");
        
//        xmlMap.put("ns",namespace);
	}
	
	@Override
	public User loadUser(String username) throws UserNotFoundException {
		if(username.contains("@")) {
            if (!XMPPServer.getInstance().isLocal(new JID(username))) {
                throw new UserNotFoundException("Cannot load user of remote server: " + username);
            }
            username = username.substring(0,username.lastIndexOf("@"));
        }
		String name = null;
		String email = null;
		
		
		try {
			String soapRequest = loadUserMethodSOAPRequest;
			Document docRequest = DocumentHelper.parseText(soapRequest);
			
			String namespace = WebServiceCaller.getNamespaceFromSOAPRequest(docRequest);
			HashMap<String, String> xmlMap = new HashMap<String, String>();
			xmlMap.put("requestns",namespace);
			
			XPath xRequest = docRequest.createXPath("//requestns:"+usernameField);
			xRequest.setNamespaceURIs(xmlMap);
            Node node = xRequest.selectSingleNode(docRequest);
            node.setText(username);
            soapRequest = docRequest.asXML();

			String result = WebServiceCaller.callWebService(wsAddress, soapRequest, loadUserMethodContentType);
			Document doc = DocumentHelper.parseText(result);
			
			namespace = WebServiceCaller.getNamespaceFromSOAPRequest(doc);
			xmlMap.put("resultns",namespace);
			
	        XPath x = doc.createXPath("//resultns:"+nameField);
	        x.setNamespaceURIs(xmlMap);
			Node no = x.selectSingleNode(doc);
			if(no != null){
				name = no.getText();
			}
			
			x = doc.createXPath("//resultns:"+emailField);
			x.setNamespaceURIs(xmlMap);
			no = x.selectSingleNode(doc);
			if(no != null){
				email = no.getText();
			}
			
			return new User(username, name, email, new Date(), new Date());
		} catch (Exception e) {
			throw new UserNotFoundException(e);
		}
	}

	@Override
	public User createUser(String username, String password, String name,
			String email) throws UserAlreadyExistsException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteUser(String username) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getUserCount() {
		int count = 0;
		try {
			String soapRequest = userCountMethodSOAPRequest;
			String result = WebServiceCaller.callWebService(wsAddress, soapRequest, userCountMethodContentType);
			Document doc = DocumentHelper.parseText(result);
			
			String namespace = WebServiceCaller.getNamespaceFromSOAPRequest(doc);
			HashMap<String, String> xmlMap = new HashMap<String, String>();
			xmlMap.put("ns",namespace);
			
			XPath x = doc.createXPath("//ns:"+userCountField);
			x.setNamespaceURIs(xmlMap);
			Node no = x.selectSingleNode(doc);
			if(no != null){
				count = Integer.parseInt(no.getText());
			}
		} catch (IOException e) {
			Log.error(e.getMessage(), e);
		} catch (DocumentException e) {
			Log.error(e.getMessage(), e);
		}
		return count;
	}

	@Override
	public Collection<User> getUsers() {
		return getUsers(0,Integer.MAX_VALUE);
	}

	@Override
	public Collection<String> getUsernames() {
		return getUsernames(0,Integer.MAX_VALUE);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<User> getUsers(int startIndex, int numResults) {
		 Collection<String> usernames = getUsernames(startIndex, numResults);
	     return new UserCollection(usernames.toArray(new String[usernames.size()]));
	}
	
	private Collection<String> getUsernames(int startIndex, int numResults) {
		List<String> usernames = new ArrayList<String>(500);
		try {
			String soapRequest = allUsersMethodSOAPRequest;
			String result = WebServiceCaller.callWebService(wsAddress, soapRequest, allUsersMethodContentType);
			Document doc = DocumentHelper.parseText(result);
			
			String namespace = WebServiceCaller.getNamespaceFromSOAPRequest(doc);
			HashMap<String, String> xmlMap = new HashMap<String, String>();
			xmlMap.put("ns",namespace);
			
			XPath x = doc.createXPath("//ns:"+usernameField);
			x.setNamespaceURIs(xmlMap);
			@SuppressWarnings("unchecked")
			List<Node> nodeList = x.selectNodes(doc);
			int count = 0;
			Node no;
			for (Iterator<Node> it = nodeList.iterator(); it.hasNext() && count <numResults; count++){
				no = (Node) it.next();
				if (count >= startIndex){
					usernames.add(no.getText());
				}
			}
			if (Log.isDebugEnabled())
            {
                Log.debug("Results: " + usernames.size());
                LogResults(usernames);
            }
			
		} catch (IOException e) {
			Log.error(e.getMessage(), e);
		} catch (DocumentException e) {
			Log.error(e.getMessage(), e);
		}
		return usernames;
	}

	@Override
	public void setName(String username, String name)
			throws UserNotFoundException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setEmail(String username, String email)
			throws UserNotFoundException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setCreationDate(String username, Date creationDate)
			throws UserNotFoundException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setModificationDate(String username, Date modificationDate)
			throws UserNotFoundException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<String> getSearchFields() throws UnsupportedOperationException {
		if (searchMethodSOAPRequest == null) {
            throw new UnsupportedOperationException();
        }
		return new LinkedHashSet<String>(Arrays.asList("Username", "Name", "Email"));
	}

	@Override
	public Collection<User> findUsers(Set<String> fields, String query)
			throws UnsupportedOperationException {
		return findUsers(fields, query, 0, Integer.MAX_VALUE);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<User> findUsers(Set<String> fields, String query,
			int startIndex, int numResults)
			throws UnsupportedOperationException {
		if (searchMethodSOAPRequest == null) {
            throw new UnsupportedOperationException();
        }
		if (fields.isEmpty()) {
			return Collections.emptyList();
		}
		if (!getSearchFields().containsAll(fields)) {
			throw new IllegalArgumentException("Search fields " + fields + " are not valid.");
		}
		if (query == null || "".equals(query)) {
			return Collections.emptyList();
		}
		List<String> usernames = new ArrayList<String>(500);
		try {
			String soapRequest = searchMethodSOAPRequest;
			Document docRequest = DocumentHelper.parseText(soapRequest);
			
			String namespace = WebServiceCaller.getNamespaceFromSOAPRequest(docRequest);
			HashMap<String, String> xmlMap = new HashMap<String, String>();
			xmlMap.put("requestns",namespace);
			
			XPath xRequest;
			if (fields.contains("Username")) {
                xRequest = docRequest.createXPath("//requestns:"+usernameField);
                xRequest.setNamespaceURIs(xmlMap);
                Node node = xRequest.selectSingleNode(docRequest);
                node.setText(query);
            }
            if (fields.contains("Name")) {
            	xRequest = docRequest.createXPath("//requestns:"+nameField);
            	xRequest.setNamespaceURIs(xmlMap);
                Node node = xRequest.selectSingleNode(docRequest);
                node.setText(query);
            }
            if (fields.contains("Email")) {
            	xRequest = docRequest.createXPath("//requestns:"+emailField);
            	xRequest.setNamespaceURIs(xmlMap);
                Node node = xRequest.selectSingleNode(docRequest);
                node.setText(query);
            }
			soapRequest = docRequest.asXML();
			String result = WebServiceCaller.callWebService(wsAddress, soapRequest, searchMethodContentType);
			Document doc = DocumentHelper.parseText(result);
			
			namespace = WebServiceCaller.getNamespaceFromSOAPRequest(doc);
			xmlMap.put("resultns",namespace);
			
			XPath x = doc.createXPath("//resultns:"+usernameField);
			x.setNamespaceURIs(xmlMap);
			List<Node> nodeList = x.selectNodes(doc);
			int count = 0;
			Node no;
			for (Iterator<Node> it = nodeList.iterator(); it.hasNext() && count <startIndex + numResults; count++){
				no = it.next();
				if (count >= startIndex){
					usernames.add(no.getText());
				}
			}
			if (Log.isDebugEnabled())
            {
                Log.debug("Results: " + usernames.size());
                LogResults(usernames);
            }
			
		} catch (IOException e) {
			Log.error(e.getMessage(), e);
		} catch (DocumentException e) {
			Log.error(e.getMessage(), e);
		}
		
		return new UserCollection(usernames.toArray(new String[usernames.size()]));
	}

	@Override
	public boolean isReadOnly() {
		return IS_READ_ONLY;
	}

	@Override
	public boolean isNameRequired() {
		return false;
	}

	@Override
	public boolean isEmailRequired() {
		return false;
	}
	
    /**
     * Make sure that Log.isDebugEnabled()==true before calling this method.
     * Twenty elements will be logged in every log line, so for 81-100 elements
     * five log lines will be generated
     * @param listElements a list of Strings which will be logged 
     */
    private void LogResults(List<String> listElements) {
        String callingMethod = Thread.currentThread().getStackTrace()[3].getMethodName();
        StringBuilder sb = new StringBuilder(256);
        int count = 0;
        for (String element : listElements)
        {
            if (count > 20)
            {
                Log.debug(callingMethod + " results: " + sb.toString());
                sb.delete(0, sb.length());
                count = 0;
            }
            sb.append(element).append(",");
            count++;
        }
        sb.append(".");
        Log.debug(callingMethod + " results: " + sb.toString());
    }
}
