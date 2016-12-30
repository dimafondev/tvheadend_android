package com.dimafon.tvhclient;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.tvheadend.tvhguide.TVHGuideApplication;
import org.tvheadend.tvhguide.model.Channel;
import org.tvheadend.tvhguide.model.ChannelTag;
import org.tvheadend.tvhguide.model.Programme;
import org.tvheadend.tvhguide.model.Recording;


public class TVHClientApplication extends TVHGuideApplication {
	
	private Collection<Long> expandedProgs = new HashSet<Long>();
	private ChannelTag currentTag;

	public Channel getChannel(long id){
		for (Channel ch : getChannels()) {
			if(ch.id==id) return ch;
		}
		return null;
	}

	public Recording getRecording(long id){
		for (Recording r : getRecordings()) {
			if(r.id==id) return r;
		}
		return null;
	}

	public void setExpanded(Programme p, boolean checked) {
		if(checked) expandedProgs.add(p.id);
		else expandedProgs.remove(p.id);
	}

	public boolean isExpanded(Programme p) {
		return p!=null && expandedProgs.contains(p.id);
	}

	
	
    public List<ChannelTag> getChannelTags() {
        List<ChannelTag> tags = super.getChannelTags();
        if(!tags.isEmpty()){
        	if(tags.get(0).id==0){
        		tags.remove(0);
        	}
        }
        return tags;
    }

	public ChannelTag getCurrentTag() {
		return currentTag;
	}
	public void setCurrentTag(ChannelTag tag) {
		currentTag = tag;
	}

}
