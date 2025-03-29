package com.fortisbank.business.services;
/**
 * PendingAccountRequestService manages the lifecycle of account creation requests
 * that require manager approval.
 *
 * Responsibilities:
 * - Stores requests for opening new accounts (except for the mandatory checking account).
 * - Allows managers to approve or reject pending account requests.
 * - Ensures requests are validated, tracked, and removed after decision.
 * - Notifies users and updates account state on approval.
 *
 * This service decouples the user flow from the administrative flow,
 * allowing asynchronous approval and oversight.
 */

public class PendingAccountRequestService {
}
