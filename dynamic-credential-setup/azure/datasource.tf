data "azurerm_subscription" "current" {
}

data "azuread_client_config" "current" {
}

data "terrakube_organization" "org" {
  name = var.terrakube_organization_name
}