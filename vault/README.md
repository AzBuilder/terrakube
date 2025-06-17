
## Steps

```
vault server -dev -dev-root-token-id="dev-only-token" & 
export VAULT_ADDR='http://localhost:8200'
vault login dev-only-token
vault auth enable userpass
vault write auth/userpass/users/terrakube password=p@ssw0rd
vault auth enable approle
vault write auth/approle/role/terrakube-role secret_id_ttl=10m token_ttl=20m token_max_ttl=30m
vault secrets enable -path shared -version 2 kv
vault kv put -mount shared kv/creds username=terrakube password=p@ssw0rd
vault auth enable jwt
vault write auth/jwt/config oidc_discovery_url="https://terrakube-api.platform.local" bound_issuer="https://terrakube-api.platform.local" 
vault policy write tfc-policy terrakube-policy.hcl
vault write auth/jwt/role/tfc-role @vault-jwt-auth-role.json
```