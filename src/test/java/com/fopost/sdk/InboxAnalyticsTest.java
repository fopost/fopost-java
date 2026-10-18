package com.fopost.sdk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.fopost.sdk.param.AnalyticsParams;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class InboxAnalyticsTest {

    @Test
    void inboxReadsTheAggregatesInTheAskedTimeZone() {
        FakeTransport transport = new FakeTransport().enqueue(200, """
                {"totals":{"received":40,"sent":22,"answered":18,"open":9,"conversations":12,"median_response_minutes":null},
                 "deltas":{"received":12.5,"sent":null,"answered":-3},
                 "series":[{"date":"2026-10-17","received":4,"sent":2,"answered":2}],
                 "by_platform":[{"platform":"linkedin","received":30,"sent":20}],
                 "by_type":[{"type":"comment","count":25}],
                 "by_account":[{"account_id":"a1","platform":"linkedin","username":"yourbrand","name":"Your Brand","avatar":null,"received":30,"sent":20}],
                 "response_time":[{"bucket":"under_1h","count":10}],
                 "heatmap":[{"weekday":1,"hour":9,"count":6}],
                 "time_zone":"Europe/Berlin"}""");

        var analytics = TestSupport.client(transport)
                .analytics()
                .inbox(AnalyticsParams.create().accountId("a1").workspaceId("w1").days(30), "Europe/Berlin");

        assertEquals("GET", transport.last().method());
        assertEquals(
                "https://api.fopost.test/v1/analytics/inbox?account_id=a1&workspace_id=w1&days=30&tz=Europe%2FBerlin",
                transport.last().url());
        assertEquals(40, analytics.totals().received());
        assertNull(analytics.totals().medianResponseMinutes());
        assertEquals(12.5, analytics.deltas().received());
        assertEquals(LocalDate.of(2026, 10, 17), analytics.series().get(0).date());
        assertEquals("yourbrand", analytics.byAccount().get(0).username());
        assertEquals("under_1h", analytics.responseTime().get(0).bucket());
        assertEquals(9, analytics.heatmap().get(0).hour());
        assertEquals("Europe/Berlin", analytics.timeZone());
    }
}
