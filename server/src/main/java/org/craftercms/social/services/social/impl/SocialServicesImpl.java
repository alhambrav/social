package org.craftercms.social.services.social.impl;

import org.craftercms.commons.mongo.MongoDataException;
import org.craftercms.social.domain.social.SocialUgc;
import org.craftercms.social.exceptions.IllegalUgcException;
import org.craftercms.social.exceptions.SocialException;
import org.craftercms.social.exceptions.UGCException;
import org.craftercms.social.repositories.ugc.UGCRepository;
import org.craftercms.social.services.social.SocialServices;
import org.craftercms.social.services.social.VoteOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class SocialServicesImpl<T extends SocialUgc> implements SocialServices {

    private UGCRepository<T> ugcRepository;
    private Logger log = LoggerFactory.getLogger(SocialServicesImpl.class);

    @Override
    public T vote(final String ugcId, final VoteOptions voteOptions, final String userId,
                  final String tenantId) throws SocialException {
        try {
            T ugc = ugcRepository.findUGC(tenantId, ugcId);
            if (ugc == null) {
                log.debug("Given UGC does not exist with that id {} for that tenant {}", ugcId, tenantId);
                throw new IllegalUgcException("Given UGC does not exist for given tenant");
            }
            switch (voteOptions) {
                case VOTE_UP:
                    voteUp(ugc, userId);
                    break;
                case VOTE_DOWN:
                    voteDown(ugc, userId);
                    break;
                case UNVOTE_DOWN:
                    unvoteDown(ugc, userId);
                case UNVOTE_UP:
                    unvoteUp(ugc, userId);
            }
            ugcRepository.save(ugc);
            return ugc;
        } catch (MongoDataException ex) {
            throw new UGCException("Unable to find UGC with given Id and TenantId");
        }
    }

    @Override
    public T moderate(final String ugcId, final SocialUgc.ModerationStatus moderationStatus, final String
        userId, final String tenant) throws UGCException {
        try{
            T ugc = ugcRepository.findUGC(ugcId, tenant);
            if(ugc==null){
                throw new IllegalUgcException("Given UGC does not exist for current user's tenant");
            }
            if(ugc.getModerationStatus() != SocialUgc.ModerationStatus.TRASH){ // Once is trash stays thrash (TBC)
                ugc.setModerationStatus(moderationStatus);
            }
            ugcRepository.save(ugc);
            return ugc;
        }catch (MongoDataException ex){
            log.debug("Unable to change ugc moderation status",ex);
            throw new UGCException("Unable to change ugc moderation status",ex);
        }
    }

    private void voteDown(final T ugc, final String userId) {
        unvoteUp(ugc,userId);
        ugc.getVotesDown().add(userId);
    }

    private void unvoteUp(final T ugc, final String userId) {
        ugc.getVotesUp().remove(userId);
    }

    protected void voteUp(final T ugc, final String userId) {
        unvoteDown(ugc, userId);
        ugc.getVotesUp().add(userId);

    }

    protected void unvoteDown(final SocialUgc ugc, final String userId) {
        ugc.getVotesDown().remove(userId);
    }

    @Override
    public T flag(final String ugcId, final String reason, final String userId) {
        return null;
    }

    @Override
    public T unFlag(final String ugcId, final String reason, final String userId) {
        return null;
    }

    public void setUgcRepository(final UGCRepository<T> ugcRepository) {
        this.ugcRepository = ugcRepository;
    }
}