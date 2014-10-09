package com.pcitc.openfire.plugin;

import java.util.Collection;

import org.jivesoftware.openfire.group.Group;
import org.jivesoftware.openfire.group.GroupAlreadyExistsException;
import org.jivesoftware.openfire.group.GroupNotFoundException;
import org.jivesoftware.openfire.group.GroupProvider;
import org.jivesoftware.util.PersistableMap;
import org.xmpp.packet.JID;

public class WebServiceGroupProvider implements GroupProvider {

	@Override
	public Group getGroup(String name) throws GroupNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Collection<String> getGroupNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<String> getGroupNames(int startIndex, int numResults) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<String> getGroupNames(JID user) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Group createGroup(String name) throws GroupAlreadyExistsException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteGroup(String name) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setName(String oldName, String newName)
			throws GroupAlreadyExistsException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDescription(String name, String description)
			throws GroupNotFoundException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isSharingSupported() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Collection<String> getSharedGroupNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<String> getSharedGroupNames(JID user) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<String> getPublicSharedGroupNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<String> getVisibleGroupNames(String userGroup) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addMember(String groupName, JID user, boolean administrator) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateMember(String groupName, JID user, boolean administrator) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteMember(String groupName, JID user) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isReadOnly() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Collection<String> search(String query) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<String> search(String query, int startIndex,
			int numResults) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<String> search(String key, String value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isSearchSupported() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public PersistableMap<String, String> loadProperties(Group group) {
		// TODO Auto-generated method stub
		return null;
	}

}
