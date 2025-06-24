provider vault {
  address = "http://localhost:8200"
  skip_tls_verify = true
  skip_child_token = true
  token = ""
  }

data "vault_kv_secret_v2" "password" {
  mount = "shared"
  name  = "kv/creds"
}


