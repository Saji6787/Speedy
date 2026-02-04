package main

import (
	"fmt"
	"io"
	"log"
	"net/http"
	"time"
)

func main() {
	http.HandleFunc("/download", handleDownload)
	http.HandleFunc("/upload", handleUpload)

	port := "8080"
	fmt.Printf("Speedy Backend Server running on port %s\n", port)
	if err := http.ListenAndServe(":"+port, nil); err != nil {
		log.Fatal(err)
	}
}

// handleDownload streams random data or a fixed buffer to the client
func handleDownload(w http.ResponseWriter, r *http.Request) {
    // 10MB chunk for testing
    const chunkSize = 10 * 1024 * 1024 
    
    // Set headers to prevent buffering/caching
    w.Header().Set("Content-Type", "application/octet-stream")
    w.Header().Set("Cache-Control", "no-cache")
    
    // Create a 1MB buffer of 'A's
    buffer := make([]byte, 1024*1024)
    for i := range buffer {
        buffer[i] = 'A'
    }
    
    // Write 10MB total (10 chunks)
    for i := 0; i < 10; i++ {
        if _, err := w.Write(buffer); err != nil {
            return
        }
        // Small flush/yield might happen automatically
    }
}

// handleUpload reads the request body and discards it to measure upload speed
func handleUpload(w http.ResponseWriter, r *http.Request) {
    if r.Method != http.MethodPost {
        http.Error(w, "Only POST allowed", http.StatusMethodNotAllowed)
        return
    }

    start := time.Now()
    
    // Read and discard body
    written, err := io.Copy(io.Discard, r.Body)
    if err != nil {
        http.Error(w, "Upload failed", http.StatusInternalServerError)
        return
    }
    
    duration := time.Since(start)
    
    // Respond with stats
    msg := fmt.Sprintf("Uploaded %d bytes in %v", written, duration)
    w.WriteHeader(http.StatusOK)
    w.Write([]byte(msg))
}
