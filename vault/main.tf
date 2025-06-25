provider vault {
  skip_tls_verify = true
  skip_child_token = true
  }

data "vault_kv_secret_v2" "password" {
  mount = "shared"
  name  = "kv/creds"
}


