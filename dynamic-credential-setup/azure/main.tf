resource "random_string" "random" {
  length  = 3
  special = false
  lower   = true
  upper   = false
  numeric = false
}

resource "azuread_application_registration" "terrakube_application" {
  display_name = "terrakube-dynamic-creds-${random_string.random.result}"
}

resource "azuread_service_principal" "terrakube_service_principal" {
  client_id                    = azuread_application_registration.terrakube_application.client_id
  app_role_assignment_required = false
  owners                       = [data.azuread_client_config.current.object_id]
}

resource "azurerm_role_assignment" "terrakube_role_assignment" {
  scope                = data.azurerm_subscription.current.id
  principal_id         = azuread_service_principal.terrakube_service_principal.object_id
  role_definition_name = "Contributor"
}

resource "azuread_application_federated_identity_credential" "federated_identity_credential" {
  application_id = azuread_application_registration.terrakube_application.id
  display_name   = "terrakube-identity-${random_string.random.result}"
  description    = "Terrakube Federated Credentials"
  audiences      = [var.terrakube_federated_credentials_audience]
  issuer         = "https://${var.terrakube_api_hostname}"
  subject        = "organization:${var.terrakube_organization_name}:workspace:${var.terrakube_workspace_name}"
}

resource "terrakube_workspace_cli" "dynamic_credentials_workspace" {
  organization_id = data.terrakube_organization.org.id
  name            = var.terrakube_workspace_name
  description     = "Sample description"
  execution_mode  = "remote"
  iac_type        = "terraform"
  iac_version     = "1.5.7"

  depends_on = [data.terrakube_organization.org]
}

resource "terrakube_workspace_variable" "env1" {
  organization_id = data.terrakube_organization.org.id
  workspace_id    = terrakube_workspace_cli.dynamic_credentials_workspace.id
  key             = "ARM_TENANT_ID"
  value           = data.azuread_client_config.current.tenant_id
  description     = "ARM_TENANT_ID"
  category        = "ENV"
  sensitive       = false
  hcl             = false
}

resource "terrakube_workspace_variable" "env2" {
  organization_id = data.terrakube_organization.org.id
  workspace_id    = terrakube_workspace_cli.dynamic_credentials_workspace.id
  key             = "ARM_SUBSCRIPTION_ID"
  value           = data.azurerm_subscription.current.subscription_id
  description     = "ARM_SUBSCRIPTION_ID"
  category        = "ENV"
  sensitive       = false
  hcl             = false
}

resource "terrakube_workspace_variable" "env3" {
  organization_id = data.terrakube_organization.org.id
  workspace_id    = terrakube_workspace_cli.dynamic_credentials_workspace.id
  key             = "ARM_CLIENT_ID"
  value           = azuread_application_registration.terrakube_application.client_id
  description     = "ARM_CLIENT_ID"
  category        = "ENV"
  sensitive       = false
  hcl             = false
}

resource "terrakube_workspace_variable" "env4" {
  organization_id = data.terrakube_organization.org.id
  workspace_id    = terrakube_workspace_cli.dynamic_credentials_workspace.id
  key             = "ARM_USE_OIDC"
  value           = "true"
  description     = "ARM_USE_OIDC"
  category        = "ENV"
  sensitive       = false
  hcl             = false
}

resource "terrakube_workspace_variable" "env5" {
  organization_id = data.terrakube_organization.org.id
  workspace_id    = terrakube_workspace_cli.dynamic_credentials_workspace.id
  key             = "ENABLE_DYNAMIC_CREDENTIALS_AZURE"
  value           = "true"
  description     = "ENABLE_DYNAMIC_CREDENTIALS_AZURE"
  category        = "ENV"
  sensitive       = false
  hcl             = false
}

resource "terrakube_workspace_variable" "env6" {
  organization_id = data.terrakube_organization.org.id
  workspace_id    = terrakube_workspace_cli.dynamic_credentials_workspace.id
  key             = "WORKLOAD_IDENTITY_AUDIENCE_AZURE"
  value           = var.terrakube_federated_credentials_audience
  description     = "WORKLOAD_IDENTITY_AUDIENCE_AZURE"
  category        = "ENV"
  sensitive       = false
  hcl             = false
}