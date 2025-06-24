
## Steps

```
bao server -dev -dev-root-token-id="dev-only-token" & 
export VAULT_ADDR='http://localhost:8200'
bao login dev-only-token
bao auth enable userpass
bao write auth/userpass/users/terrakube password=p@ssw0rd
bao auth enable approle
bao write auth/approle/role/terrakube-role secret_id_ttl=10m token_ttl=20m token_max_ttl=30m
bao secrets enable -path shared -version 2 kv
bao kv put -mount shared kv/creds username=terrakube password=p@ssw0rd
bao auth enable jwt
bao write auth/jwt/config oidc_discovery_url="https://terrakube-api.platform.local" bound_issuer="https://terrakube-api.platform.local" 
bao policy write tfc-policy terrakube-policy.hcl
bao write auth/jwt/role/tfc-role @vault-jwt-auth-role.json
```