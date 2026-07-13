FROM devopsfaith/krakend:2.6

# Copy configuration
COPY krakend.json /etc/krakend/krakend.json

# Expose Krakend port
EXPOSE 8080

# Run Krakend
CMD ["krakend", "run", "-c", "/etc/krakend/krakend.json"]