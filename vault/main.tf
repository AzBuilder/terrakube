provider vault {
  address = "http://localhost:8200"
  skip_tls_verify = true
  skip_child_token = true
  
}

data "vault_kv_secret_v2" "username" {
  mount = "shared/data/kv/creds"
  name  = "username"
}

data "vault_kv_secret_v2" "password" {
  mount = "shared/data/kv/creds"
  name  = "password"
}


