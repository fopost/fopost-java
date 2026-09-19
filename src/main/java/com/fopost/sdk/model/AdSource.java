package com.fopost.sdk.model;

import java.util.List;

/** One ads connection with the ad accounts and Pages its grant reaches. */
public record AdSource(
        String connectionId,
        String name,
        String workspaceId,
        List<AdAccount> adAccounts,
        List<MetaPage> pages,
        String error) {

    public record AdAccount(String id, String name, String currency, Integer status) {}

    public record MetaPage(String id, String name, String instagramUserId) {}
}
