require recipes-extended/xen/xen.inc

# Intial tag of xilinx-2016.3-rc2 tag
SRCREV = "b49f2bffb7fb64eb8e9835161afeaca642ad0bac"

XEN_REL="4.7"

PV = "${XEN_REL}.0+git${SRCPV}"

S = "${WORKDIR}/git"

FILESEXTRAPATHS_append := " \
                ${THISDIR}/files: \
                "

FILES_${PN}-xl += " \
    /etc/xen/example-passnet.cfg \
    /etc/xen/example-pvnet.cfg \
    /etc/xen/example-simple.cfg \
    /etc/xen/passthrough-example-part.dtb \
    "

SRC_URI = " \
    git://github.com/Xilinx/xen.git;protocol=https \
    file://example-passnet.cfg \
    file://example-pvnet.cfg \
    file://example-simple.cfg \
    file://passthrough-example-part.dts \
    "

SRC_URI[md5sum] = "5c244ba649faab65db00ae9ad54e2f00"
SRC_URI[sha256sum] = "0e31451ec62fafb6dc623f19bdc15ea400231127aca07bc27de8b69e01968995"

DEPENDS += "u-boot-mkimage-native"

EXTRA_OEMAKE += 'CROSS_COMPILE=${TARGET_PREFIX}'

XENIMAGE_KERNEL_LOADADDRESS ?= "0x5000000"

do_compile_append() {
    dtc -I dts -O dtb ${WORKDIR}/passthrough-example-part.dts -o ${WORKDIR}/passthrough-example-part.dtb
}

do_deploy_append() {
    if [ -f ${DEPLOYDIR}/xen-${MACHINE} ]; then
        uboot-mkimage -A arm64 -T kernel \
        -a ${XENIMAGE_KERNEL_LOADADDRESS} \
        -e ${XENIMAGE_KERNEL_LOADADDRESS} \
        -C none \
        -d ${DEPLOYDIR}/xen-${MACHINE} ${DEPLOYDIR}/xen.ub
    fi
}

do_install_append() {
    install -d -m 0755 ${D}/etc/xen
    install -m 0644 ${WORKDIR}/example-passnet.cfg ${D}/etc/xen/example-passnet.cfg
    install -m 0644 ${WORKDIR}/example-pvnet.cfg ${D}/etc/xen/example-pvnet.cfg
    install -m 0644 ${WORKDIR}/example-simple.cfg ${D}/etc/xen/example-simple.cfg

    install -m 0644 ${WORKDIR}/passthrough-example-part.dtb ${D}/etc/xen/passthrough-example-part.dtb
}
