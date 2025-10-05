variable "project_name" {
  type    = string
  default = "tse-lab"
}

variable "region" {
  type    = string
  default = "sa-east-1"
}

variable "public_ssh_key" {
  type = string
}

variable "instance_type" {
  type    = string
  default = "t3.small"
}

variable "db_username" {
  type      = string
  sensitive = true
}

variable "db_password" {
  type      = string
  sensitive = true
}

variable "wf_admin_user" {
  type    = string
  default = "admin"
}

variable "wf_admin_password" {
  type      = string
  sensitive = true
}
