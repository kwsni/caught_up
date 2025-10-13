package com.kwsni.caught_up.social.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class MemberFollowKey implements Serializable {
    @Column(name = "follower_id")
    private Long followerId;

    @Column(name = "followed_id")
    private Long followedId;

    protected MemberFollowKey() {}

    public MemberFollowKey(Long followerId, Long followedId) {
        this.followerId = followerId;
        this.followedId = followedId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((followerId == null) ? 0 : followerId.hashCode());
        result = prime * result + ((followedId == null) ? 0 : followedId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MemberFollowKey other = (MemberFollowKey) obj;
        if (followerId == null) {
            if (other.followerId != null)
                return false;
        } else if (!followerId.equals(other.followerId))
            return false;
        if (followedId == null) {
            if (other.followedId != null)
                return false;
        } else if (!followedId.equals(other.followedId))
            return false;
        return true;
    }

    public Long getFollowerId() {
        return followerId;
    }

    public void setFollowerId(Long followerId) {
        this.followerId = followerId;
    }

    public Long getFollowedId() {
        return followedId;
    }

    public void setFollowedId(Long followedId) {
        this.followedId = followedId;
    }
    
}
