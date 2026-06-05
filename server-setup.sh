#!/bin/bash
# ──────────────────────────────────────────────────────────────────
# Hetzner VPS — İlk Kurulum Scripti
# Ubuntu 24.04 LTS için
# Çalıştır: bash server-setup.sh
# ──────────────────────────────────────────────────────────────────

set -e

echo "==> Sistem güncelleniyor..."
apt-get update -qq && apt-get upgrade -y -qq

echo "==> Docker kuruluyor..."
curl -fsSL https://get.docker.com | sh
systemctl enable docker
systemctl start docker

echo "==> Docker Compose plugin kuruluyor..."
apt-get install -y -qq docker-compose-plugin

echo "==> Uygulama dizini oluşturuluyor..."
mkdir -p /opt/ard
cd /opt/ard

echo "==> Repolar klonlanıyor..."
git clone https://github.com/ilbarsseckin/ard-backend
git clone https://github.com/ilbarsseckin/ard-frontend

echo "==> Deploy klasörü oluşturuluyor..."
mkdir -p deploy/certbot/conf deploy/certbot/www

echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "  Kurulum tamamlandı!"
echo "  Sıradaki adımlar:"
echo ""
echo "  1. .env.prod dosyasını oluştur:"
echo "     cp deploy/.env.prod.template deploy/.env.prod"
echo "     nano deploy/.env.prod"
echo ""
echo "  2. SSL sertifikası al (önce DNS yönlendir!):"
echo "     docker run -it --rm \\"
echo "       -v /opt/ard/deploy/certbot/conf:/etc/letsencrypt \\"
echo "       -v /opt/ard/deploy/certbot/www:/var/www/certbot \\"
echo "       -p 80:80 certbot/certbot certonly --standalone \\"
echo "       -d baskiurunleri.com -d www.baskiurunleri.com \\"
echo "       -d api.baskiurunleri.com"
echo ""
echo "  3. Stack'i başlat:"
echo "     docker compose -f deploy/docker-compose.prod.yml \\
          --env-file deploy/.env.prod up -d"
echo ""
echo "  4. Veritabanını seed'le:"
echo "     docker compose -f deploy/docker-compose.prod.yml \\
          exec db psql -U baski_user -d baski_db -f /seed.sql"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
