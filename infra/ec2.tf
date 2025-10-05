# Ubuntu 24.04 LTS (Noble) x86_64
data "aws_ami" "ubuntu" {
  owners      = ["099720109477"]
  most_recent = true

  filter {
    name   = "name"
    values = ["ubuntu/images/hvm-ssd/ubuntu-noble-24.04-amd64-server-*"]
  }
}

resource "aws_key_pair" "demo" {
  key_name   = "${var.project_name}-key"
  public_key = var.public_ssh_key
}

resource "aws_instance" "app" {
  ami                         = data.aws_ami.ubuntu.id
  instance_type               = var.instance_type
  subnet_id                   = aws_subnet.public_a.id
  vpc_security_group_ids      = [aws_security_group.web.id]
  key_name                    = aws_key_pair.demo.key_name
  associate_public_ip_address = true
  user_data = templatefile("${path.module}/user-data.sh.tpl", {
    app_image_uri     = aws_ecr_repository.app.repository_url
    db_username       = var.db_username
    db_password       = var.db_password
    wf_admin_user     = var.wf_admin_user
    wf_admin_password = var.wf_admin_password
  })

  tags = { Name = "${var.project_name}-ec2" }
}
