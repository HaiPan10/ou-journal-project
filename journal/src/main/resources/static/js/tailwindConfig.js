fetch('../config.json')
    .then(response => response.json())
    .then(config => {
        tailwind.config = {
            theme: {
                extend: {
                    colors: {
                        'main': config.mainColor,
                    }
                }
            }
        };
    });

