data "tls_certificate" "terrakube_certificate" {
  url = "https://${var.terrakube_api_hostname}"
}

data "terrakube_organization" "org" {
  name = var.terrakube_organization_name
}