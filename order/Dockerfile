FROM node:14

ENV NODE_ENV production
ENV APP_DIR /home/node/app/

EXPOSE 3000

RUN mkdir ${APP_DIR}
WORKDIR ${APP_DIR}

ADD https://raw.githubusercontent.com/vishnubob/wait-for-it/master/wait-for-it.sh ${APP_DIR}
RUN chmod +x ${APP_DIR}/wait-for-it.sh
COPY node_modules/ ${APP_DIR}/node_modules/
COPY src/ ${APP_DIR}/src/
COPY package*.json knexfile.js ${APP_DIR}

RUN npm install

CMD ["npm","start"]