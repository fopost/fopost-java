package com.fopost.sdk.model;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;

/** Rows exactly as Google returns them. */
public record GoogleQueryResult(List<JsonNode> rows) {}
