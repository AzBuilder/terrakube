terraform {
  cloud {
    hostname = "terrakube-api.platform.local"
    organization = "simple"
    workspaces {
      name = "vault"
    }
  }
}