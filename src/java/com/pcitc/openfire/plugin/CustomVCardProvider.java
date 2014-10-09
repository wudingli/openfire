package com.pcitc.openfire.plugin;

import java.util.HashMap;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.XPath;
import org.jivesoftware.openfire.user.User;
import org.jivesoftware.openfire.user.UserManager;
import org.jivesoftware.openfire.vcard.DefaultVCardProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomVCardProvider extends DefaultVCardProvider {
	private static final Logger Log = LoggerFactory.getLogger(CustomVCardProvider.class);
	public CustomVCardProvider() {
		super();
	}
	
	@Override
	public Element loadVCard(String username) {
        synchronized (username.intern()) {
        	Element vCardElement = super.loadVCard(username);
        	if(vCardElement == null){
        		try {
					User user = UserManager.getUserProvider().loadUser(username);
					String name = user.getName();
					String xml = "<vCard xmlns=\"vcard-temp\"><N><FAMILY></FAMILY>"
+"<GIVEN></GIVEN>"
+"<MIDDLE/>"
+"</N>"
+"<ORG><ORGNAME/>"
+"<ORGUNIT/>"
+"</ORG>"
+"<FN></FN>"
+"<URL/>"
+"<TITLE/>"
+"<NICKNAME/>"
+"<EMAIL><HOME/><INTERNET/><PREF/><USERID/>"
+"</EMAIL>"
+"<TEL><PAGER/><WORK/><NUMBER/>"
+"</TEL>"
+"<TEL><CELL/><WORK/><NUMBER/>"
+"</TEL>"
+"<TEL><VOICE/><WORK/><NUMBER/>"
+"</TEL>"
+"<TEL><FAX/><WORK/><NUMBER/>"
+"</TEL>"
+"<TEL><PAGER/><HOME/><NUMBER/>"
+"</TEL>"
+"<TEL><CELL/><HOME/><NUMBER/>"
+"</TEL>"
+"<TEL><VOICE/><HOME/><NUMBER/>"
+"</TEL>"
+"<TEL><FAX/><HOME/><NUMBER/>"
+"</TEL>"
+"<ADR><WORK/><PCODE/>"
+"<REGION/>"
+"<STREET/>"
+"<CTRY/>"
+"<LOCALITY/>"
+"</ADR>"
+"<ADR><HOME/><PCODE/>"
+"<REGION/>"
+"<STREET/>"
+"<CTRY/>"
+"<LOCALITY/>"
+"</ADR>"
+"</vCard>";
					Document doc = DocumentHelper.parseText(xml);
					vCardElement = doc.getRootElement();
					String namespace = vCardElement.getNamespaceURI();
					
					HashMap<String, String> xmlMap = new HashMap<String, String>();
					xmlMap.put("ns",namespace);
					
					XPath xPath = doc.createXPath("//ns:NICKNAME");
					xPath.setNamespaceURIs(xmlMap);
		            Node node = xPath.selectSingleNode(vCardElement);
		            node.setText(name);

				} catch (Exception e) {
					Log.error("Error loading User of username: " + username, e);
				}
        	}            
            return vCardElement;
        }
    }

}
