package com.kwsni.caught_up.social.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;

@Entity
public class MemberFollow {
    @EmbeddedId
    MemberFollowKey id;

    @ManyToOne
    @MapsId("followerId")
    @JoinColumn(name = "follower_id")
    Member follower;

    @ManyToOne
    @MapsId("followedId")
    @JoinColumn(name = "followed_id")
    Member followed;

    @CreationTimestamp
    LocalDateTime followDate;

    protected MemberFollow() {}

    public MemberFollow(Member follower, Member followed) {
        this.follower = follower;
        this.followed = followed;
        this.id = new MemberFollowKey(follower.getId(), followed.getId());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((follower == null) ? 0 : follower.hashCode());
        result = prime * result + ((followed == null) ? 0 : followed.hashCode());
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
        MemberFollow other = (MemberFollow) obj;
        if (follower == null) {
            if (other.follower != null)
                return false;
        } else if (!follower.equals(other.follower))
            return false;
        if (followed == null) {
            if (other.followed != null)
                return false;
        } else if (!followed.equals(other.followed))
            return false;
        return true;
    }

    public MemberFollowKey getId() {
        return id;
    }

    public void setId(MemberFollowKey id) {
        this.id = id;
    }

    public Member getFollower() {
        return follower;
    }

    public void setFollower(Member follower) {
        this.follower = follower;
    }

    public Member getFollowed() {
        return followed;
    }

    public void setFollowed(Member followed) {
        this.followed = followed;
    }

    public LocalDateTime getFollowDate() {
        return followDate;
    }

    public void setFollowDate(LocalDateTime followDate) {
        this.followDate = followDate;
    }
    
}
