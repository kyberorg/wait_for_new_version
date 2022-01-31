package main

import (
	"bytes"
	"fmt"
	"io"
	"os"
	"os/exec"
	"strings"
)

func main() {
	cmdName := "/app/wait4version"
	args := os.Args[1:]

	fmt.Printf("Running %s %s", cmdName, strings.Join(args, " "))
	cmd := exec.Command(cmdName, args...)
	var stdBuffer bytes.Buffer
	mw := io.MultiWriter(os.Stdout, &stdBuffer)

	cmd.Stdout = mw
	cmd.Stderr = mw

	err := cmd.Run()
	if err != nil {
		_, _ = fmt.Fprintf(os.Stderr, "Failed to start err=%v\n", err)
		fmt.Println(stdBuffer.String())
		os.Exit(1)
	}
	fmt.Printf("Output \n%s\n", stdBuffer.String())
}
