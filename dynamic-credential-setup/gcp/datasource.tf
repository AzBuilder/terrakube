data "google_project" "project" {
}

data "terrakube_organization" "org" {
  name = var.terrakube_organization_name
}