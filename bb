#!/bin/bash

# bb - Simple wrapper script for Yocto bitbake commands with a menu selection

ENV_FILE="$BB_TOPDIR/.env"

TARGET_IMAGE="imx-image-core-mini"
TARGET_KERNEL="linux-imx"
TARGET_UBOOT="u-boot-imx"
TARGET_IMXBOOT="imx-boot"
TARGET_INITRD="fsl-image-mfgtool-initramfs"

if [ -z "$BBPATH" ]; then
    echo "Error: BBPATH is not set."
    echo "Please run: source wsmn-165-imx-setup-release.sh"
    exit 1
fi

if [ -f "$ENV_FILE" ]; then
    source "$ENV_FILE"
    echo ".env file loaded."
else
    echo ".env file not found."
fi

usage() {
	cat << EOF
Usage:
  bb image                - bake $TARGET_IMAGE
  bb kernel               - bake $TARGET_KERNEL
  bb uboot                - bake $TARGET_UBOOT
  bb imxboot              - bake $TARGET_IMXBOOT
  bb initrd               - bake $TARGET_INITRD

  bb rebuild <target>     - cleansstate + bake
  bb rebuild_full         - cleansstate for all targets and rebuild image

  bb config               - Setup build environment (create .env)
  bb copy_to_flash_folder - Copy generated images to the flash folder for flashing
  bb fetch                - Fetch Yocto packages

  bb                      - interactive menu
EOF
    exit 0
}

config() {
    echo "Configuring environment..."
    
    if [ ! -f "$ENV_FILE" ]; then
        echo ".env file not found. Please input the flash folder path for images."
        echo -n "Enter the folder path to store images for flashing: "
        read -r flash_folder_path
        
        if [ ! -d "$flash_folder_path" ]; then
            echo "Directory does not exist. Creating it now..."
            mkdir -p "$flash_folder_path"
        fi
        
        echo "BB_FLASH_FOLDER=$flash_folder_path" > "$ENV_FILE"
        echo ".env file created with BB_FLASH_FOLDER=$flash_folder_path"
    else
        echo ".env file found. Loaded configuration."
        source "$ENV_FILE"
    fi
    
    echo "Configuration completed."
}

show_config() {
    if [ -f "$ENV_FILE" ]; then
        source "$ENV_FILE"
		echo
        echo "Current Configuration:"
        echo "BB_FLASH_FOLDER: $BB_FLASH_FOLDER"
    else
        echo ".env file not found. Configuration is not set."
    fi
}

build_image() {
    echo "Building $TARGET_IMAGE..."
    $DO bitbake $TARGET_IMAGE
}

build_kernel() {
    echo "Building $TARGET_KERNEL..."
    $DO bitbake $TARGET_KERNEL
}

build_uboot() {
    echo "Building $TARGET_UBOOT..."
    $DO bitbake $TARGET_UBOOT
}

build_imxboot() {
    echo "Building $TARGET_IMXBOOT..."
    $DO bitbake $TARGET_IMXBOOT
}

build_initrd() {
    echo "Building $TARGET_INITRD..."
    $DO bitbake $TARGET_INITRD
}

rebuild_image() {
    echo "Rebuilding $TARGET_IMAGE..."
    $DO bitbake -c cleansstate $TARGET_IMAGE
    $DO bitbake $TARGET_IMAGE
}

rebuild_kernel() {
    echo "Rebuilding $TARGET_KERNEL..."
    $DO bitbake -c cleansstate $TARGET_KERNEL
    $DO bitbake $TARGET_KERNEL
}

rebuild_uboot() {
    echo "Rebuilding $TARGET_UBOOT..."
    $DO bitbake -c cleansstate $TARGET_UBOOT
    $DO bitbake $TARGET_UBOOT
}

rebuild_imxboot() {
    echo "Rebuilding $TARGET_IMXBOOT..."
    $DO bitbake -c cleansstate $TARGET_IMXBOOT
    $DO bitbake $TARGET_IMXBOOT
}

rebuild_initrd() {
    echo "Rebuilding $TARGET_INITRD..."
    $DO bitbake -c cleansstate $TARGET_INITRD
    $DO bitbake $TARGET_INITRD
}

rebuild_full() {
    echo "Performing full rebuild (cleansstate + build all)..."
    $DO bitbake -c cleansstate $TARGET_IMXBOOT
    $DO bitbake -c cleansstate $TARGET_UBOOT
    $DO bitbake -c cleansstate $TARGET_KERNEL
    $DO bitbake -c cleansstate $TARGET_IMAGE
    $DO bitbake $TARGET_IMAGE
}

copy_to_flash_folder() {
    echo "Copying generated images to the flash folder..."

    if [ -z "$BB_FLASH_FOLDER" ]; then
        echo "BB_FLASH_FOLDER not set. Please run 'bb config' first."
        exit 1
    fi

    IMAGE_DIR="$BBPATH/tmp/deploy/images/$BB_MACHINE"

    if [ ! -d "$IMAGE_DIR" ]; then
        echo "Cannot access: $IMAGE_DIR"
        exit 1
    fi

    mkdir -p "$BB_FLASH_FOLDER"

    FILE1="imx-boot-${BB_MACHINE}-sd.bin-flash_evk"
    FILE2="imx-image-core-mini-${BB_MACHINE}.rootfs.wic.zst"
    FILE3="Image-${BB_MACHINE}.bin"
    FILE4="imx8mm-wsmn165.dtb"
    FILE5="imx8mm-wsmn165-mfgtool.dtb"
    FILE6="fsl-image-mfgtool-initramfs-${BB_MACHINE}.cpio.zst.u-boot"

    for f in "$FILE1" "$FILE2" "$FILE3" "$FILE4" "$FILE5" "$FILE6"; do
        if [ -f "$IMAGE_DIR/$f" ]; then
            cp -v "$IMAGE_DIR/$f" "$BB_FLASH_FOLDER"
        else
            echo "Warning: $f not found in $IMAGE_DIR"
        fi
    done

    echo "Images copied to $BB_FLASH_FOLDER for flashing."
}

fetch_packages() {
    echo "Fetching Yocto packages..."
    $DO bitbake $TARGET_IMAGE --runall=fetch
}

show_env() {
    echo "SDK Environments:"
    echo "-----------------"
    echo "BBPATH:     $BBPATH"
	echo "BB_DISTRO:  $BB_DISTRO"
    echo "BB_MACHINE: $BB_MACHINE"
	echo "BB_TOPDIR:  $BB_TOPDIR"
    echo

    echo "Bitbake Environments:"
    echo "---------------------"
    $DO bitbake -e $TARGET_IMAGE | grep -E '^(DISTRO_FEATURES|MACHINE_FEATURES|IMAGE_FEATURES|IMAGE_INSTALL|IMAGE_ROOTFS|IMAGE_ROOTFS_SIZE)=' --color
}

show_menu() {
	echo
    echo "Please select an option:"
	echo "------------------------"
    echo -e "  [ 1] bake $TARGET_IMAGE\t\t| Build image for production"
    echo -e "  [ 2] bake $TARGET_KERNEL\t\t\t| Build Linux kernel"
    echo -e "  [ 3] bake $TARGET_UBOOT\t\t\t| Build U-Boot"
    echo -e "  [ 4] bake $TARGET_IMXBOOT\t\t\t| Create composite bootloader (SPL + U-Boot)"
    echo -e "  [ 5] bake $TARGET_INITRD\t| Generate Initramfs image"
    echo
    echo "Rebuild targets:"
	echo "------------------------"
    echo -e "  [ 6] rebuild_full\t\t\t| Clean + rebuild all targets (imxboot, uboot, kernel, image)"
    echo -e "  [ 7] rebuild $TARGET_IMAGE\t| Clean + rebuild image"
    echo -e "  [ 8] rebuild $TARGET_KERNEL\t\t| Clean + rebuild kernel"
    echo -e "  [ 9] rebuild $TARGET_UBOOT\t\t| Clean + rebuild U-Boot"
    echo -e "  [10] rebuild $TARGET_IMXBOOT\t\t\t| Clean + rebuild composite bootloader"
    echo -e "  [11] rebuild $TARGET_INITRD| Clean + rebuild initramfs"
    echo
    echo "Other actions:"
	echo "------------------------"
    echo -e "  [12] config\t\t\t\t| Setup build environment"
    echo -e "  [13] show config\t\t\t| Display current configuration"
    echo -e "  [14] fetch\t\t\t\t| Fetch Yocto packages"
    echo -e "  [15] copy_to_flash_folder\t\t| Copy images to the flash folder for flashing"
    echo -e "  [16] show env\t\t\t| show bitbake environments"
    echo
	echo -e "   [q] Exit"
    echo -e -n "\nEnter the number (1-16): "
 
    read -r choice

    case "$choice" in
        1) build_image ;;
        2) build_kernel ;;
        3) build_uboot ;;
        4) build_imxboot ;;
        5) build_initrd ;;
        6) rebuild_full ;;
        7) rebuild_image ;;
        8) rebuild_kernel ;;
        9) rebuild_uboot ;;
        10) rebuild_imxboot ;;
        11) rebuild_initrd ;;
        12) config ;;
        13) show_config ;;
        14) fetch_packages ;;
        15) copy_to_flash_folder ;;
		16) show_env ;;
		q) exit 0 ;;
        *)
            echo "Invalid choice. Please enter a number between 1 and 16."
            show_menu
            ;;
    esac
}

# Main logic: check if argument is provided
if [ "$#" -gt 0 ]; then
    case "$1" in
        -h|--help|help) usage ;;
        image)   build_image ;;
        kernel)  build_kernel ;;
        uboot)   build_uboot ;;
        imxboot) build_imxboot ;;
        initrd)  build_initrd ;;
        rebuild)
            case "$2" in
                image)   rebuild_image ;;
                kernel)  rebuild_kernel ;;
                uboot)   rebuild_uboot ;;
                imxboot) rebuild_imxboot ;;
                initrd)  rebuild_initrd ;;
                *)
                    echo "Unknown rebuild target: $2"
                    usage
                    ;;
            esac
            ;;
        rebuild_full) rebuild_full ;;
		config) config ;;
        copy_to_flash_folder) copy_to_flash_folder ;;
        fetch) fetch_packages ;;
        env) show_env ;;
        *) usage ;;
    esac
else
    show_menu
fi
