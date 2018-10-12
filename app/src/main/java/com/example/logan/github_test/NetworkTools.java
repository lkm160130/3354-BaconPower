package com.example.logan.github_test;

import android.util.Log;

import java.util.List;

import eu.bittrade.libs.steemj.SteemJ;
import eu.bittrade.libs.steemj.apis.database.models.state.Discussion;
import eu.bittrade.libs.steemj.base.models.DiscussionQuery;
import eu.bittrade.libs.steemj.enums.DiscussionSortType;
import eu.bittrade.libs.steemj.exceptions.SteemCommunicationException;
import eu.bittrade.libs.steemj.exceptions.SteemResponseException;

public class NetworkTools {

    public static void getDsoundTrending(SteemJ steemJ) throws SteemResponseException, SteemCommunicationException {

        DiscussionQuery discussionQuery = new DiscussionQuery();
        discussionQuery.setTag("dsound");
        discussionQuery.setLimit(5);

        List<Discussion> discussions
                = steemJ.getDiscussionsBy(discussionQuery, DiscussionSortType.GET_DISCUSSIONS_BY_TRENDING);

        for (Discussion d: discussions) {
            Log.d("dsound", d.getUrl());
        }

    }

}
