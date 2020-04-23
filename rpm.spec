%define __spec_install_post %{nil}
%define __os_install_post %{nil}
AutoReqProv: no
Name:		myapp
Version:	0.0.0
Release:	0
BuildArch:	noarch
Summary:	See https://github.com/buildcom/product-services
Group:		Applications/Internet
License:	Copyright Build.com, Inc
URL:		http://www.build.com
Source0:	%{name}.jar
BuildRoot:	%(mktemp -ud %{_tmppath}/%{name}-%{version}-%{release}-XXXXXX)
%description
For more information visit https://github.com/buildcom/product-services.

%prep
%setup -T -c %{name}-%{version}

%install
mkdir -p -m0755 %{buildroot}/opt/%{name}/
mv %{_sourcedir}/%{name}.jar %{buildroot}/opt/%{name}/%{name}.jar

%files
%defattr(-,javaapp,javaapp,-)
/opt/%{name}/

%pretrans
if [ -e /etc/systemd/system/%{name}.service ]; then
	/usr/bin/systemctl stop %{name} || true
fi

%posttrans
if [ -e /etc/systemd/system/%{name}.service ]; then
	/usr/bin/systemctl stop %{name} || true
	if [ "${1}z" = "0z" ]; then
		/usr/bin/systemctl start %{name}
	fi
fi
